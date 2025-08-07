package org.netway.dongnehankki.post.repository;

import org.netway.dongnehankki.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Override
    @EntityGraph(attributePaths = {"user", "store", "images", "postHashtags", "postHashtags.hashtag"})
    Optional<Post> findById(Long postId);

    Page<Post> findByStore_StoreId(Long storeId, Pageable pageable);
}
