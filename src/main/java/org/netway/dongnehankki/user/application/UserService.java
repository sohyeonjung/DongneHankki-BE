package org.netway.dongnehankki.user.application;

import org.netway.dongnehankki.notification.dto.request.FCMTokenRequest;
import org.netway.dongnehankki.user.dto.request.CustomerSignUpRequest;
import org.netway.dongnehankki.user.dto.request.LoginRequest;
import org.netway.dongnehankki.user.dto.request.LoginResponse;
import org.netway.dongnehankki.user.dto.request.OwnerSignUpRequest;
import org.netway.dongnehankki.user.dto.request.UpdateUserRequest;
import org.netway.dongnehankki.user.dto.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserResponse customerSignUp(CustomerSignUpRequest customerSignUpRequest);
    UserResponse ownerSignUp(OwnerSignUpRequest ownerSignUpRequest);
    LoginResponse login(LoginRequest loginRequest);
    LoginResponse reissueTokens(String refreshToken);
    UserResponse findByUserId(Long userId);
    UserResponse updateUser(Long userId, UpdateUserRequest updateUserRequest);
    boolean checkLoginId(String loginId);
    Boolean checkNickname(String nickname);
    void deleteUser(Long userId);
    void updateFcmToken(Long userId, FCMTokenRequest fcmTokenRequest);
    void updateProfileImage(Long userId, MultipartFile profileImage);
}
