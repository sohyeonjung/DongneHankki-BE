package org.netway.dongnehankki.notification.presentation;

import org.netway.dongnehankki.global.auth.CustomUserDetails;
import org.netway.dongnehankki.global.common.ApiResponse;
import org.netway.dongnehankki.notification.dto.request.FCMTokenRequest;
import org.netway.dongnehankki.user.application.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "알림", description = "FCM 알림 관련 API")
@RestController
@RequestMapping("/api/fcm")
@RequiredArgsConstructor
public class FCMController {

	private final UserServiceImpl userService;

	@Operation(summary = "fcm 토큰 등록", description = "발급받은 fcm 토큰을 등록합니다.")
	@PatchMapping("/token")
	public ResponseEntity<ApiResponse<Void>> updateFCMToken(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody FCMTokenRequest fcmTokenRequest
	) {
		userService.updateFcmToken(userDetails.getUser().getUserId(), fcmTokenRequest);
		return ResponseEntity.ok(ApiResponse.success());
	}
}
