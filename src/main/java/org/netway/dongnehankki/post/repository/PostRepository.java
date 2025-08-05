package org.netway.dongnehankki.post.repository;

import org.netway.dongnehankki.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
