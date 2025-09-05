package org.netway.dongnehankki.recommendation.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.netway.dongnehankki.global.auth.CustomUserDetails;
import org.netway.dongnehankki.global.common.ApiResponse;
import org.netway.dongnehankki.recommendation.application.RecommendationService;
import org.netway.dongnehankki.post.domain.Post;
import org.netway.dongnehankki.post.dto.response.PostResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "추천", description = "추천 관련 API")
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Operation(summary = "사용자 맞춤 게시글 추천", description = "사용자의 좋아요 기록을 기반으로 맞춤 게시글을 추천합니다.")
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getRecommendedPosts(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Parameter(description = "가져올 게시글 수") @RequestParam(defaultValue = "10") int limit) {

        Long userId = userDetails.getUser().getUserId(); // 로그인한 사용자 ID

        List<Post> recommendedPosts = recommendationService.getRecommendedPosts(userId, limit);

        // Post 엔티티를 PostResponse DTO로 변환
        // 추천 게시글의 isLiked는 기본적으로 false로 설정 (사용자가 아직 좋아요를 누르지 않은 게시글이므로)
        List<PostResponse> response = recommendedPosts.stream()
            .map(post -> PostResponse.fromEntity(post))
            .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
