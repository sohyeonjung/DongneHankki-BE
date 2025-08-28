package org.netway.dongnehankki.user.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.netway.dongnehankki.global.common.ApiResponse;
import org.netway.dongnehankki.user.application.CoolSmsService;
import org.netway.dongnehankki.user.application.UserService;
import org.netway.dongnehankki.user.dto.request.CustomerSignUpRequest;
import org.netway.dongnehankki.user.dto.request.LoginRequest;
import org.netway.dongnehankki.user.dto.request.LoginResponse;
import org.netway.dongnehankki.user.dto.request.OwnerSignUpRequest;
import org.netway.dongnehankki.user.dto.request.RefreshTokenRequest;
import org.netway.dongnehankki.user.dto.request.UpdateUserRequest;
import org.netway.dongnehankki.user.dto.response.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저", description = "유저 회원가입, 로그인, 정보 관리 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CoolSmsService coolSmsService;

    @Operation(summary = "일반 유저 회원가입", description = "일반 유저(소비자)로 회원가입합니다.")
    @PostMapping("/customers")
    public ResponseEntity<ApiResponse<Void>> signUpCustomer(
        @RequestBody CustomerSignUpRequest customerSignUpRequest) {
        userService.customerSignUp(customerSignUpRequest);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "사장님 유저 회원가입", description = "사장님 유저로 회원가입합니다.")
    @PostMapping("/owners")
    public ResponseEntity<ApiResponse<Void>> signUpOwner(
        @RequestBody OwnerSignUpRequest ownerSignUpRequest) {
        userService.ownerSignUp(ownerSignUpRequest);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "로그인 ID 중복 확인", description = "회원가입 시 로그인 ID의 중복 여부를 확인합니다.")
    @GetMapping("/users/check/loginId")
    public ResponseEntity<ApiResponse<Boolean>> checkDuplicateLoginId(
        @Parameter(description = "확인할 로그인 ID") @RequestParam String loginId) {
        Boolean isAvailable = userService.checkLoginId(loginId);
        String message = isAvailable ? "사용 가능합니다." : "이미 사용 중입니다.";
        return ResponseEntity.ok(ApiResponse.success(isAvailable, message));
    }

    @Operation(summary = "닉네임 중복 확인", description = "회원가입 또는 정보 수정 시 닉네임의 중복 여부를 확인합니다.")
    @GetMapping("/users/check/nickname")
    public ResponseEntity<ApiResponse<Boolean>> checkDuplicateNickname(
        @Parameter(description = "확인할 닉네임") @RequestParam String nickname) {
        Boolean isAvailable = userService.checkNickname(nickname);
        String message = isAvailable ? "사용 가능합니다." : "이미 사용 중입니다.";
        return ResponseEntity.ok(ApiResponse.success(isAvailable, message));
    }

    @Operation(summary = "로그인", description = "로그인 ID와 비밀번호로 로그인하고 Access Token과 Refresh Token을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = userService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(loginResponse));
    }

    @Operation(summary = "토큰 재발급", description = "Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급받습니다.")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
        @RequestBody RefreshTokenRequest refreshTokenRequest) {
        LoginResponse loginResponse = userService.reissueTokens(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(loginResponse));
    }

    @Operation(summary = "유저 정보 조회", description = "유저 ID로 특정 유저의 정보를 조회합니다.")
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(
        @Parameter(description = "조회할 유저 ID") @PathVariable Long userId) {
        UserResponse userResponse = userService.findByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }

    @Operation(summary = "유저 정보 수정", description = "유저 ID로 특정 유저의 정보를 수정합니다.")
    @PatchMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
        @Parameter(description = "수정할 유저 ID") @PathVariable Long userId,
        @Valid @RequestBody UpdateUserRequest updateUserRequest) {

        UserResponse userResponse = userService.updateUser(userId, updateUserRequest);
        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }

    @Operation(summary = "회원 탈퇴", description = "유저 ID로 특정 유저를 탈퇴 처리합니다.")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
        @Parameter(description = "탈퇴할 유저 ID") @PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success());
    }


    @Operation(summary = "휴대폰 인증번호 발송", description = "지정된 휴대폰 번호로 인증번호를 발송합니다.")
    @PostMapping("/sendAuthCode")
    public ResponseEntity<ApiResponse<Void>> sendAuthCode(
        @Parameter(description = "인증번호를 받을 휴대폰 번호") @RequestParam String receiverNumber) {
        coolSmsService.sendSms(receiverNumber);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "휴대폰 인증번호 확인", description = "수신된 인증번호가 유효한지 확인합니다.")
    @GetMapping("/checkAuthCode")
    public ResponseEntity<ApiResponse<String>> checkAuthCode(
        @Parameter(description = "인증번호를 받은 휴대폰 번호") @RequestParam String receiverNumber,
        @Parameter(description = "사용자가 입력한 인증번호") @RequestParam String authCode) {
        coolSmsService.verifyAuthCode(receiverNumber, authCode);
        return ResponseEntity.ok(ApiResponse.success("인증에 성공하였습니다."));

    }

}
