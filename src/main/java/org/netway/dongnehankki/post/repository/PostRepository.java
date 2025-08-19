package org.netway.dongnehankki.post.repository;

import org.netway.dongnehankki.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Override
    @EntityGraph(attributePaths = {"user", "store", "images", "postHashtags", "postHashtags.hashtag"})
    Optional<Post> findById(Long postId);

    List<Post> findByStore_StoreIdAndPostIdLessThanOrderByPostIdDesc(Long storeId, Long cursorPostId, Pageable pageable);

    List<Post> findByStore_StoreIdOrderByPostIdDesc(Long storeId, Pageable pageable);

    List<Post> findByStoreInAndPostIdLessThanOrderByPostIdDesc(List<org.netway.dongnehankki.store.domain.Store> stores, Long cursorPostId, Pageable pageable);

    List<Post> findByStoreInOrderByPostIdDesc(List<org.netway.dongnehankki.store.domain.Store> stores, Pageable pageable);
}
