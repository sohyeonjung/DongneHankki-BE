package org.netway.dongnehankki.follow.presentation;

import lombok.RequiredArgsConstructor;
import org.netway.dongnehankki.follow.application.FollowService;
import org.netway.dongnehankki.global.auth.CustomUserDetails;
import org.netway.dongnehankki.global.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/store/{storeId}")
    public ResponseEntity<ApiResponse<Void>> follow(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long storeId) {
        followService.follow(userDetails.getUser().getUserId(), storeId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @DeleteMapping("/store/{storeId}")
    public ResponseEntity<ApiResponse<Void>> unfollow(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long storeId) {
        followService.unfollow(userDetails.getUser().getUserId(), storeId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
