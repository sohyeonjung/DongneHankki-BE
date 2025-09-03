package org.netway.dongnehankki.post.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.netway.dongnehankki.global.auth.CustomUserDetails;
import org.netway.dongnehankki.global.common.ApiResponse;
import org.netway.dongnehankki.post.application.PostLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "게시글 좋아요", description = "게시글 좋아요/취소 API")
@RestController
@RequestMapping("/api/posts/{postId}/likes")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    @Operation(summary = "게시글 좋아요", description = "사용자가 특정 게시글에 '좋아요'를 누릅니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> likePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "좋아요를 누를 게시글 ID") @PathVariable Long postId) {
        postLikeService.likePost(userDetails.getUser().getUserId(), postId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "게시글 좋아요 취소", description = "사용자가 특정 게시글에 눌렀던 '좋아요'를 취소합니다.")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> unlikePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "좋아요를 취소할 게시글 ID") @PathVariable Long postId) {
        postLikeService.unlikePost(userDetails.getUser().getUserId(), postId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
