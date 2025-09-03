package org.netway.dongnehankki.follow.repository;

import org.netway.dongnehankki.follow.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByUser_UserIdAndStore_StoreId(Long userId, Long storeId);

    List<Follow> findByUser_UserId(Long userId);

    boolean existsByUser_UserIdAndStore_StoreId(Long userId, Long postId);

    Long countByStore_StoreId(Long storeId);
}
