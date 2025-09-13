package org.netway.dongnehankki.post.application;

public interface PostLikeService {
    void likePost(Long userId, Long postId);
    void unlikePost(Long userId, Long postId);
}
