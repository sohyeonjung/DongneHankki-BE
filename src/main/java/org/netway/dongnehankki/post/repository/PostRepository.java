package org.netway.dongnehankki.post.repository;

import org.netway.dongnehankki.post.domain.Post;
import org.netway.dongnehankki.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Override
    @EntityGraph(attributePaths = {"user", "store", "images", "postHashtags",
        "postHashtags.hashtag"})
    Optional<Post> findById(Long postId);

    List<Post> findByStore_StoreIdAndPostIdLessThanOrderByPostIdDesc(Long storeId,
        Long cursorPostId, Pageable pageable);

    List<Post> findByStore_StoreIdOrderByPostIdDesc(Long storeId, Pageable pageable);

    List<Post> findByStore_StoreIdAndRoleAndPostIdLessThanOrderByPostIdDesc(Long storeId,
        Post.Role role, Long cursorPostId, Pageable pageable);

    List<Post> findByStore_StoreIdAndRoleOrderByPostIdDesc(Long storeId, Post.Role role,
        Pageable pageable);

    List<Post> findByStoreInAndPostIdLessThanOrderByPostIdDesc(List<Store> stores,
        Long cursorPostId, Pageable pageable);

    List<Post> findByStoreInOrderByPostIdDesc(List<Store> stores, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "store", "images", "postHashtags",
        "postHashtags.hashtag", "postLikes"})
    List<Post> findAllByOrderByPostIdDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"user", "store", "images", "postHashtags",
        "postHashtags.hashtag", "postLikes"})
    List<Post> findAllByPostIdLessThanOrderByPostIdDesc(Long cursorPostId, Pageable pageable);

    // --- 새로 추가되는 메소드 ---

    // 콜드 스타트용: 최신 게시글 N개 조회
    @EntityGraph(attributePaths = {"user", "store", "images", "postHashtags",
        "postHashtags.hashtag", "postLikes"})
    List<Post> findTopByOrderByCreatedAtDesc(Pageable pageable);

    // 해시태그 기반 추천: 관심 해시태그를 포함하고, 이미 좋아요 누른 게시글은 제외
    @Query("SELECT p FROM Post p JOIN p.postHashtags ph JOIN ph.hashtag h " +
        "WHERE h.name IN :hashtags AND p.postId NOT IN :excludePostIds " +
        "ORDER BY p.createdAt DESC")
    @EntityGraph(attributePaths = {"user", "store", "images", "postHashtags",
        "postHashtags.hashtag", "postLikes"})
    List<Post> findRecommendedPostsByHashtags(@Param("hashtags") List<String> hashtags,
        @Param("excludePostIds") List<Long> excludePostIds,
        Pageable pageable);

}
