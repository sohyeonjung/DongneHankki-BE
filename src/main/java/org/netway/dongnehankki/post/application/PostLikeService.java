package org.netway.dongnehankki.post.application;

import lombok.RequiredArgsConstructor;
import org.netway.dongnehankki.post.domain.Post;
import org.netway.dongnehankki.post.domain.PostLike;
import org.netway.dongnehankki.post.exception.AlreadyLikedException;
import org.netway.dongnehankki.post.exception.NotLikedException;
import org.netway.dongnehankki.post.exception.UnregisteredPostException;
import org.netway.dongnehankki.post.repository.PostLikeRepository;
import org.netway.dongnehankki.post.repository.PostRepository;
import org.netway.dongnehankki.user.domain.User;
import org.netway.dongnehankki.user.exception.UnregisteredUserException;
import org.netway.dongnehankki.user.infrastructure.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public void likePost(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UnregisteredUserException::new);
        Post post = postRepository.findById(postId)
                .orElseThrow(UnregisteredPostException::new);

        postLikeRepository.findByUser_UserIdAndPost_PostId(userId, postId)
                .ifPresent(like -> {
                    throw new AlreadyLikedException();
                });

        PostLike postLike = PostLike.of(user, post);
        postLikeRepository.save(postLike);
    }

    @Transactional
    public void unlikePost(Long userId, Long postId) {
        PostLike postLike = postLikeRepository.findByUser_UserIdAndPost_PostId(userId, postId)
                .orElseThrow(() -> new NotLikedException());
        postLikeRepository.delete(postLike);
    }
}
