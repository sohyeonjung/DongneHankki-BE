package org.netway.dongnehankki.store.infrastructure.repository;

import org.netway.dongnehankki.store.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
