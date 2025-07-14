package org.netway.dongnehankki.user.presentation;

import lombok.RequiredArgsConstructor;
import org.netway.dongnehankki.global.common.ApiResponse;
import org.netway.dongnehankki.user.application.UserService;
import org.netway.dongnehankki.user.dto.login.LoginRequest;
import org.netway.dongnehankki.user.dto.login.LoginResponse;
import org.netway.dongnehankki.user.dto.login.RefreshTokenRequest;
import org.netway.dongnehankki.user.dto.response.UserResponse;
import org.netway.dongnehankki.user.dto.signUp.CustomerSignUpRequest;
import org.netway.dongnehankki.user.dto.signUp.OwnerSignUpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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

}
