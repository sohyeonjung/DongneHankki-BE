package org.netway.dongnehankki.store.infrastructure.repository;

import java.util.List;

import org.netway.dongnehankki.store.domain.Review;
import org.netway.dongnehankki.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findAllByUser(User user);
}
