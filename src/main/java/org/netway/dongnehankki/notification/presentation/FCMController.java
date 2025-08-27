package org.netway.dongnehankki.notification.presentation;

import org.netway.dongnehankki.global.auth.CustomUserDetails;
import org.netway.dongnehankki.global.common.ApiResponse;
import org.netway.dongnehankki.notification.dto.request.FCMTokenRequest;
import org.netway.dongnehankki.user.application.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/fcm")
@RequiredArgsConstructor
public class FCMController {

	private final UserService userService;


	@PatchMapping("/token")
	public ResponseEntity<ApiResponse<Void>> updateFCMToken(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody FCMTokenRequest fcmTokenRequest
	) {
		userService.updateFcmToken(userDetails.getUser().getUserId(), fcmTokenRequest);
		return ResponseEntity.ok(ApiResponse.success());
	}
}
