package org.netway.dongnehankki.post.application;

import lombok.RequiredArgsConstructor;
import org.netway.dongnehankki.global.util.S3Service;
import org.netway.dongnehankki.post.domain.Hashtag;
import org.netway.dongnehankki.post.domain.Image;
import org.netway.dongnehankki.post.domain.Post;
import org.netway.dongnehankki.post.domain.PostHashtag;
import org.netway.dongnehankki.post.dto.request.PostCreateRequest;
import org.netway.dongnehankki.post.exception.PostNotFoundException;
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
import org.springframework.data.domain.Page;
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
    public Page<PostResponse> getPostsByStore(Long storeId, Pageable pageable) {
        return postRepository.findByStore_StoreId(storeId, pageable)
                .map(PostResponse::fromEntity);
    }
}
