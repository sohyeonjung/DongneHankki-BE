package org.netway.dongnehankki.recommendation.application;

import lombok.RequiredArgsConstructor;
import org.netway.dongnehankki.post.repository.PostRepository;
import org.netway.dongnehankki.post.repository.PostLikeRepository;
import org.netway.dongnehankki.post.repository.HashtagRepository;
import org.netway.dongnehankki.post.repository.PostHashtagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;

import org.netway.dongnehankki.post.domain.Post;
import org.netway.dongnehankki.post.domain.PostLike;
import org.netway.dongnehankki.post.domain.PostHashtag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;

    @Transactional(readOnly = true)
    public List<Post> getRecommendedPosts(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);

        // 1. 개인화된 추천 게시글 (해시태그 기반)
        List<PostLike> likedPosts = postLikeRepository.findByUser_UserId(userId);
        List<Post> personalizedRecommendations;

        if (likedPosts.isEmpty()) {
            // 좋아요 누른 게시글이 없으면, 최신 게시글을 추천 (콜드 스타트 해결)
            personalizedRecommendations = postRepository.findTopByOrderByCreatedAtDesc(pageable);
        } else {
            // 좋아요 누른 게시글들의 해시태그 추출 및 빈도 계산
            Map<String, Long> hashtagFrequencies = likedPosts.stream()
                .flatMap(postLike -> postLike.getPost().getPostHashtags().stream())
                .map(postHashtag -> postHashtag.getHashtag().getName())
                .collect(Collectors.groupingBy(name -> name, Collectors.counting()));

            // 가장 빈도가 높은 해시태그 (사용자 관심 해시태그) 추출
            List<String> topHashtags = hashtagFrequencies.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5) // 상위 5개 해시태그만 사용
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

            List<Long> likedPostIds = likedPosts.stream()
                .map(postLike -> postLike.getPost().getPostId())
                .collect(Collectors.toList());

            personalizedRecommendations = postRepository.findRecommendedPostsByHashtags(topHashtags, likedPostIds, pageable);
        }

        // 2. 인기 게시글 가져오기 (두 단계 쿼리)
        List<Long> popularPostIds = postRepository.findTopNPopularPostIds(pageable);
        List<Post> popularRecommendations = postRepository.findAllById(popularPostIds);


        // 3. 두 목록을 합치고 중복 제거 후 최종 limit에 맞춰 반환
        Set<Post> combinedRecommendations = new HashSet<>();
        combinedRecommendations.addAll(personalizedRecommendations);
        combinedRecommendations.addAll(popularRecommendations);

        // 최종 추천 목록을 limit에 맞춰 자르기
        return combinedRecommendations.stream()
            .limit(limit)
            .collect(Collectors.toList());
    }
}
