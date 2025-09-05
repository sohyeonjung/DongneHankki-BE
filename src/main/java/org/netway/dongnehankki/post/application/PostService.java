package org.netway.dongnehankki.post.application;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.netway.dongnehankki.follow.repository.FollowRepository;
import org.netway.dongnehankki.global.util.S3Service;
import org.netway.dongnehankki.post.domain.Hashtag;
import org.netway.dongnehankki.post.domain.Post;
import org.netway.dongnehankki.post.domain.Post.Role;
import org.netway.dongnehankki.post.domain.PostLike;
import org.netway.dongnehankki.post.dto.request.PostCreateRequest;
import org.netway.dongnehankki.post.dto.request.PostUpdateRequest;
import org.netway.dongnehankki.post.dto.response.CursorResult;
import org.netway.dongnehankki.post.exception.UnregisteredPostException;
import org.netway.dongnehankki.post.exception.UserNotMatchedException;
import org.netway.dongnehankki.post.repository.CommentRepository;
import org.netway.dongnehankki.post.repository.HashtagRepository;
import org.netway.dongnehankki.post.repository.ImageRepository;
import org.netway.dongnehankki.post.repository.PostHashtagRepository;
import org.netway.dongnehankki.post.repository.PostLikeRepository;
import org.netway.dongnehankki.post.repository.PostRepository;
import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.store.exception.UnregisteredStoreException;
import org.netway.dongnehankki.store.infrastructure.repository.StoreRepository;
import org.netway.dongnehankki.user.domain.User;
import org.netway.dongnehankki.user.exception.UnregisteredUserException;
import org.netway.dongnehankki.user.infrastructure.UserRepository;
import org.netway.dongnehankki.post.dto.response.PostResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final S3Service s3Service;
    private final FollowRepository followRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final VertexAIService vertexAIService;


    @Transactional
    public void createPost(PostCreateRequest request, Long userId, Post.Role role) {
        User user = userRepository.findById(userId)
            .orElseThrow(UnregisteredUserException::new);
        Store store = storeRepository.findById(request.getStoreId())
            .orElseThrow(UnregisteredStoreException::new);

        Post post = Post.createPost(request.getContent(), store, user, role);

        if (request.getImages() != null) {
            for (int i = 0; i < request.getImages().length; i++) {
                String imageUrl = s3Service.uploadFile(request.getImages()[i], "post-images");
                post.addImage(imageUrl, i);
            }
        }

        request.getHashtags().forEach(tagName -> {
            Hashtag hashtag = hashtagRepository.findByName(tagName)
                .orElseGet(() -> hashtagRepository.save(Hashtag.createHashtag(tagName)));
            post.addPostHashtag(hashtag);
        });

        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId, Long userId) {
        Post post =
            postRepository.findById(postId).orElseThrow(UnregisteredPostException::new);
        boolean postLike = postLikeRepository.existsByUser_UserIdAndPost_PostId(userId, post.getPostId());
        int commentCount = commentRepository.countByPost_PostId(post.getPostId());
        return PostResponse.fromEntity(post,postLike, commentCount);
    }

    @Transactional(readOnly = true)
    public CursorResult<PostResponse> getPostsByStore(Long storeId, Long cursorPostId, int pageSize) {
        final Pageable pageable = PageRequest.of(0, pageSize + 1);
        final List<Post> posts = (cursorPostId == null) ?
            postRepository.findByStore_StoreIdOrderByPostIdDesc(storeId, pageable) :
            postRepository.findByStore_StoreIdAndPostIdLessThanOrderByPostIdDesc(storeId, cursorPostId, pageable);

        Long nextCursor = null;
        List<Post> responsePosts = posts;

        if (posts.size() > pageSize) {
            nextCursor = posts.get(pageSize).getPostId();
            responsePosts = posts.subList(0, pageSize);
        }

        List<PostResponse> response = responsePosts.stream()
            .map(PostResponse::fromEntity)
            .toList();

        return new CursorResult<>(response, nextCursor);
    }

    @Transactional(readOnly = true)
    public CursorResult<PostResponse> getPostsByStoreAndRole(Long storeId, Role role, Long cursorPostId, int pageSize) {
        final Pageable pageable = PageRequest.of(0, pageSize + 1);
        final List<Post> posts = (cursorPostId == null) ?
            postRepository.findByStore_StoreIdAndRoleOrderByPostIdDesc(storeId, role, pageable) :
            postRepository.findByStore_StoreIdAndRoleAndPostIdLessThanOrderByPostIdDesc(storeId, role, cursorPostId, pageable);

        Long nextCursor = null;
        List<Post> responsePosts = posts;

        if (posts.size() > pageSize) {
            nextCursor = posts.get(pageSize).getPostId();
            responsePosts = posts.subList(0, pageSize);
        }

        List<PostResponse> response = responsePosts.stream()
            .map(PostResponse::fromEntity)
            .toList();

        return new CursorResult<>(response, nextCursor);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(UnregisteredPostException::new);
        if(!post.getUser().getUserId().equals(userId)){
            throw new UserNotMatchedException();
        }
        post.markAsDeleted();
    }

    @Transactional
    public void updatePost(Long postId, PostUpdateRequest request, Long userId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(UnregisteredPostException::new);
        if (!post.getUser().getUserId().equals(userId)) {
            throw new UserNotMatchedException();
        }

        if (request.getDeleteImageIds() != null && !request.getDeleteImageIds().isEmpty()) {
            List<org.netway.dongnehankki.post.domain.Image> imagesToDelete = post.getImages().stream()
                .filter(image -> request.getDeleteImageIds().contains(image.getImageId()))
                .toList();

            imagesToDelete.forEach(image -> {
                s3Service.deleteFile(image.getUrl());
                post.getImages().remove(image);
            });
        }

        List<Hashtag> newHashtags = request.getHashtags().stream()
            .map(tagName -> hashtagRepository.findByName(tagName)
                .orElseGet(() -> hashtagRepository.save(Hashtag.createHashtag(tagName))))
            .toList();

        post.update(request.getContent(), newHashtags);
    }

    @Transactional(readOnly = true)
    public CursorResult<PostResponse> getPostsFromFollowedStores(Long userId, Long cursorPostId, int pageSize) {
        final Pageable pageable = PageRequest.of(0, pageSize + 1);
        List<Store> followedStores = followRepository.findByUser_UserId(userId).stream()
                .map(follow -> follow.getStore())
                .toList();

        final List<Post> posts = (cursorPostId == null) ?
                postRepository.findByStoreInOrderByPostIdDesc(followedStores, pageable) :
                postRepository.findByStoreInAndPostIdLessThanOrderByPostIdDesc(followedStores, cursorPostId, pageable);

        Long nextCursor = null;
        List<Post> responsePosts = posts;

        if (posts.size() > pageSize) {
            nextCursor = posts.get(pageSize).getPostId();
            responsePosts = posts.subList(0, pageSize);
        }

        List<PostResponse> response = responsePosts.stream()
                .map(PostResponse::fromEntity)
                .toList();

        return new CursorResult<>(response, nextCursor);
    }

    @Transactional(readOnly = true)
    public CursorResult<PostResponse> latestPosts(Long cursorPostId, int pageSize) {
        final Pageable pageable = PageRequest.of(0, pageSize + 1);
        final List<Post> posts = (cursorPostId == null) ?
            postRepository.findAllByOrderByPostIdDesc(pageable) :
            postRepository.findAllByPostIdLessThanOrderByPostIdDesc(cursorPostId, pageable);

        Long nextCursor = null;
        List<Post> responsePosts = posts;

        if (posts.size() > pageSize) {
            nextCursor = posts.get(pageSize).getPostId();
            responsePosts = posts.subList(0, pageSize);
        }

        List<PostResponse> response = responsePosts.stream()
            .map(PostResponse::fromEntity)
            .toList();

        return new CursorResult<>(response, nextCursor);
    }

    @Transactional
    public String generatePost(Long storeId, String text, MultipartFile image) throws IOException {
        Store store = storeRepository.findById(storeId).orElseThrow(UnregisteredStoreException::new);

        String menuNames = store.getMenus().stream().map(it -> it.getName())
            .collect(Collectors.joining(", "));

        String prompt = String.format("""
         너는 전문적인 SNS 마케팅 콘텐츠 기획자이자 카피라이터야.
         아래 제공되는 [입력 정보]와 [사진]을 분석하여, 인스타그램과 같은 SNS 채널에 게시할 2~3문장의 마케팅 게시글을 작성해줘.
        
         - **핵심 지시사항**:
           1.  **사진을 최우선으로 분석**: 사진 속 음식, 분위기, 색감, 스타일을 철저히 분석하고, 이를 글에 녹여내야 해.
           2.  **가게 정보 활용**: 가게의 이름, 종류, 메뉴를 마케팅에 효과적으로 활용해줘. 특히 가게 종류는 업종 코드 대신 업종명을 참고해서 작성해줘.
           3.  **가게 주인의 글 활용**: 가게 주인이 작성한 글의 의도를 파악하고, 그 내용을 포함하거나 확장해서 자연스럽게 글을 완성해줘.
           4.  **톤 앤 매너**: 친근하고 매력적인 말투로 사용자의 흥미를 유발해야 해.
           5.  **해시태그**: 관련성 높은 해시태그 3~5개를 추천해줘.
        
         ---
         
         **[입력 정보]**
         이름: %s
         종류(업종명): %s
         메뉴: %s
         가게 주인의 글: %s
         
        **[출력 형식]**
         마케팅 글과 해시태그만 출력해줘. 어떠한 서두나 제목도 붙이지 마.
         json형식으로만 [content : "", tag: ["tag1","tag2"]] 이런식으로만 응답해줘 "```json" 이런 마크다운도 절대 달지마 그냥 json 형식으로만 응답해
          
        \s
         마케팅 글과 해시태그 외에 다른 내용은 절대 포함하지 마.
         
         **[참고 가게 종류 표]**
            | 업종명 | 업종코드 |
            | --- | --- |
            | (축산물, 정육점/축산물, 정육점) | 2102 |
            | (수산물, 건어물/수산물, 건어물) | 2103 |
            | (농산물, 청과물/농산물, 청과물) | 2104 |
            | (기타식음료품/기타식음료품) | 2105 |
            | (건강보조식품/건강보조식품) | 2201 |
            | (일반음식점/일반음식점) | 2301 |
            | (중식전문점/중식전문점) | 2302 |
            | (일식전문점/일식전문점) | 2303 |
            | (서양식전문점/서양식전문점) | 2305 |
            | (치킨전문점/치킨전문점) | 2309 |
            | (식음료(기타)/식음료(기타)) | 2310 |
            | (제과, 제빵/제과, 제빵) | 2501 |
            | (커피전문점/커피전문점 | 2502 |
            | (일반주점/일반주점) | 2601 |
            | (슈퍼마켓, 마트/슈퍼마켓, 마트) | 5201 |
            | (편의점/편의점) | 5202 |
         """, store.getName(), store.getIndustryCode(), menuNames, text);

        return vertexAIService.generatePost(image, prompt);
    }
}
