package org.netway.dongnehankki.recommendation.application;

import java.util.List;
import org.netway.dongnehankki.post.domain.Post;

public interface RecommendationService {
    List<Post> getRecommendedPosts(Long userId, int limit);
}
