package org.netway.dongnehankki.recommendation.application;

import lombok.RequiredArgsConstructor;
import org.netway.dongnehankki.post.repository.PostRepository;
import org.netway.dongnehankki.post.repository.PostLikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;

import org.netway.dongnehankki.post.domain.Post;
import org.netway.dongnehankki.post.domain.PostLike;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService{

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional(readOnly = true)
    public List<Post> getRecommendedPosts(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);

        List<PostLike> likedPosts = postLikeRepository.findByUser_UserId(userId);
        List<Post> personalizedRecommendations;

        if (likedPosts.isEmpty()) {
            personalizedRecommendations = postRepository.findTopByOrderByCreatedAtDesc(pageable);
        } else {
            Map<String, Long> hashtagFrequencies = likedPosts.stream()
                .flatMap(postLike -> postLike.getPost().getPostHashtags().stream())
                .map(postHashtag -> postHashtag.getHashtag().getName())
                .collect(Collectors.groupingBy(name -> name, Collectors.counting()));

            List<String> topHashtags = hashtagFrequencies.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

            List<Long> likedPostIds = likedPosts.stream()
                .map(postLike -> postLike.getPost().getPostId())
                .collect(Collectors.toList());

            personalizedRecommendations = postRepository.findRecommendedPostsByHashtags(topHashtags, likedPostIds, pageable);
        }

        List<Long> popularPostIds = postRepository.findTopNPopularPostIds(pageable);
        List<Post> popularRecommendations = postRepository.findAllById(popularPostIds);

        Set<Post> combinedRecommendations = new HashSet<>();
        combinedRecommendations.addAll(personalizedRecommendations);
        combinedRecommendations.addAll(popularRecommendations);

        return combinedRecommendations.stream()
            .limit(limit)
            .collect(Collectors.toList());
    }
}
