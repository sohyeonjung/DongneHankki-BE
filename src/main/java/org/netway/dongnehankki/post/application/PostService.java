package org.netway.dongnehankki.post.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.netway.dongnehankki.global.util.S3Service;
import org.netway.dongnehankki.post.domain.Hashtag;
import org.netway.dongnehankki.post.domain.Post;
import org.netway.dongnehankki.post.dto.request.PostCreateRequest;
import org.netway.dongnehankki.post.dto.request.PostUpdateRequest;
import org.netway.dongnehankki.post.dto.response.CursorResult;
import org.netway.dongnehankki.post.exception.PostNotFoundException;
import org.netway.dongnehankki.post.exception.UserNotMatchedException;
import org.netway.dongnehankki.post.repository.HashtagRepository;
import org.netway.dongnehankki.post.repository.ImageRepository;
import org.netway.dongnehankki.post.repository.PostHashtagRepository;
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

    @Transactional
    public void createPost(PostCreateRequest request, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(UnregisteredUserException::new);
        Store store = storeRepository.findById(request.getStoreId())
            .orElseThrow(UnregisteredStoreException::new);

        Post post = Post.createPost(request.getContent(), store, user);

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
    public PostResponse getPost(Long postId) {
        return postRepository.findById(postId)
                .map(PostResponse::fromEntity)
                .orElseThrow(() -> new PostNotFoundException());
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

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(PostNotFoundException::new);
        if(!post.getUser().getUserId().equals(userId)){
            throw new UserNotMatchedException();
        }
        post.markAsDeleted();
    }

    @Transactional
    public void updatePost(Long postId, PostUpdateRequest request, Long userId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(PostNotFoundException::new);
        if (!post.getUser().getUserId().equals(userId)) {
            throw new UserNotMatchedException();
        }

        // Delete images
        if (request.getDeleteImageIds() != null && !request.getDeleteImageIds().isEmpty()) {
            List<org.netway.dongnehankki.post.domain.Image> imagesToDelete = post.getImages().stream()
                .filter(image -> request.getDeleteImageIds().contains(image.getImageId()))
                .toList();

            imagesToDelete.forEach(image -> {
                s3Service.deleteFile(image.getUrl());
                post.getImages().remove(image);
            });
        }

        // Update hashtags
        List<Hashtag> newHashtags = request.getHashtags().stream()
            .map(tagName -> hashtagRepository.findByName(tagName)
                .orElseGet(() -> hashtagRepository.save(Hashtag.createHashtag(tagName))))
            .toList();

        post.update(request.getContent(), newHashtags);
    }
}
