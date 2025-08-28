package org.netway.dongnehankki.follow.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.netway.dongnehankki.follow.application.FollowService;
import org.netway.dongnehankki.global.auth.CustomUserDetails;
import org.netway.dongnehankki.global.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "팔로우", description = "가게 팔로우/언팔로우 API")
@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @Operation(summary = "가게 팔로우", description = "사용자가 특정 가게를 팔로우합니다.")
    @SecurityRequirement(name = "bearer-key")
    @PostMapping("/store/{storeId}")
    public ResponseEntity<ApiResponse<Void>> follow(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "팔로우할 가게 ID") @PathVariable Long storeId) {
        followService.follow(userDetails.getUser().getUserId(), storeId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "가게 언팔로우", description = "사용자가 팔로우했던 가게를 언팔로우합니다.")
    @SecurityRequirement(name = "bearer-key")
    @DeleteMapping("/store/{storeId}")
    public ResponseEntity<ApiResponse<Void>> unfollow(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "언팔로우할 가게 ID") @PathVariable Long storeId) {
        followService.unfollow(userDetails.getUser().getUserId(), storeId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
