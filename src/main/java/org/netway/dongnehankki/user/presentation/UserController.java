package org.netway.dongnehankki.user.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.netway.dongnehankki.global.common.ApiResponse;
import org.netway.dongnehankki.user.application.CoolSmsService;
import org.netway.dongnehankki.user.application.UserService;
import org.netway.dongnehankki.user.dto.request.LoginRequest;
import org.netway.dongnehankki.user.dto.request.LoginResponse;
import org.netway.dongnehankki.user.dto.request.RefreshTokenRequest;
import org.netway.dongnehankki.user.dto.request.UpdateUserRequest;
import org.netway.dongnehankki.user.dto.response.UserResponse;
import org.netway.dongnehankki.user.dto.request.CustomerSignUpRequest;
import org.netway.dongnehankki.user.dto.request.OwnerSignUpRequest;
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

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CoolSmsService coolSmsService;

    @PostMapping("/customers")
    public ResponseEntity<ApiResponse<Void>> signUpCustomer(@RequestBody CustomerSignUpRequest customerSignUpRequest) {
        userService.customerSignUp(customerSignUpRequest);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PostMapping("/owners")
    public ResponseEntity<ApiResponse<Void>> signUpOwner(@RequestBody OwnerSignUpRequest ownerSignUpRequest) {
        userService.ownerSignUp(ownerSignUpRequest);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping("/users/check/loginId")
    public ResponseEntity<ApiResponse<Boolean>> checkDuplicateLoginId(@RequestParam String loginId){
        Boolean isAvailable = userService.checkLoginId(loginId);
        String message = isAvailable ? "사용 가능합니다." : "이미 사용 중입니다.";
        return ResponseEntity.ok(ApiResponse.success(isAvailable, message));
    }

    @GetMapping("/users/check/nickname")
    public ResponseEntity<ApiResponse<Boolean>> checkDuplicateNickname(@RequestParam String nickname){
        Boolean isAvailable = userService.checkNickname(nickname);
        String message = isAvailable ? "사용 가능합니다." : "이미 사용 중입니다.";
        return ResponseEntity.ok(ApiResponse.success(isAvailable, message));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = userService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(loginResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        LoginResponse loginResponse = userService.reissueTokens(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(loginResponse));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long userId){
        UserResponse userResponse = userService.findByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }

    @PatchMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable Long userId, @Valid @RequestBody
        UpdateUserRequest updateUserRequest){

        UserResponse userResponse = userService.updateUser(userId, updateUserRequest);
        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId){
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success());
    }


    @PostMapping("/sendAuthCode")
    public ResponseEntity<ApiResponse<Void>> sendOne(@RequestParam String receiverNumber) {
        coolSmsService.sendSms(receiverNumber);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping("/checkAuthCode")
    public ResponseEntity<ApiResponse<String>> checkCertificationNumber(@RequestParam String receiverNumber,
        @RequestParam String authCode) {
        if (coolSmsService.verifyAuthCode(receiverNumber, authCode)){
            return ResponseEntity.ok(ApiResponse.success("인증에 성공하였습니다."));
        } else {
            return ResponseEntity.ok(ApiResponse.error("401","유효하지않은 인증 번호 입니다."));
        }
    }

}
