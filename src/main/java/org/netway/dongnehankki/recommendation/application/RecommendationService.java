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
        // 1. 사용자가 좋아요를 누른 게시글 목록 가져오기
        List<PostLike> likedPosts = postLikeRepository.findByUser_UserId(userId);

        Pageable pageable = PageRequest.of(0, limit);

        if (likedPosts.isEmpty()) {
            // 좋아요 누른 게시글이 없으면, 최신 게시글을 추천 (콜드 스타트 해결)
            return postRepository.findTopByOrderByCreatedAtDesc(pageable);
        }

        // 2. 좋아요 누른 게시글들의 해시태그 추출 및 빈도 계산
        Map<String, Long> hashtagFrequencies = likedPosts.stream()
            .flatMap(postLike -> postLike.getPost().getPostHashtags().stream())
            .map(postHashtag -> postHashtag.getHashtag().getName())
            .collect(Collectors.groupingBy(name -> name, Collectors.counting()));

        // 3. 가장 빈도가 높은 해시태그 (사용자 관심 해시태그) 추출
        List<String> topHashtags = hashtagFrequencies.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5) // 상위 5개 해시태그만 사용
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        // 4. 추천 후보 게시글 조회
        //    - 사용자의 관심 해시태그를 포함하는 게시글
        //    - 사용자가 아직 좋아요를 누르지 않은 게시글
        //    - 최신 게시글 위주로
        List<Long> likedPostIds = likedPosts.stream()
            .map(postLike -> postLike.getPost().getPostId())
            .collect(Collectors.toList());

        return postRepository.findRecommendedPostsByHashtags(topHashtags, likedPostIds, pageable);
    }
}
