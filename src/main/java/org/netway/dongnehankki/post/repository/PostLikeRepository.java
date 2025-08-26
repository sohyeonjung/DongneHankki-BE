package org.netway.dongnehankki.post.repository;

import org.netway.dongnehankki.post.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByUser_UserIdAndPost_PostId(Long userId, Long postId);
}
