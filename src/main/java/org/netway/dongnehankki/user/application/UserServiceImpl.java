package org.netway.dongnehankki.user.application;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.netway.dongnehankki.global.auth.CustomUserDetails;
import org.netway.dongnehankki.global.auth.jwt.JwtTokenProvider;
import org.netway.dongnehankki.global.auth.jwt.RefreshToken;
import org.netway.dongnehankki.global.auth.jwt.RefreshTokenRepository;
import org.netway.dongnehankki.global.util.S3Service;
import org.netway.dongnehankki.notification.dto.request.FCMTokenRequest;
import org.netway.dongnehankki.store.domain.Review;
import org.netway.dongnehankki.store.infrastructure.repository.ReviewRepository;
import org.netway.dongnehankki.user.dto.request.UpdateUserRequest;
import org.netway.dongnehankki.user.exception.DuplicateNickNameException;
import org.netway.dongnehankki.user.exception.DuplicateLoginIdException;
import org.netway.dongnehankki.user.exception.InvalidPasswordException;
import org.netway.dongnehankki.store.exception.UnregisteredStoreException;
import org.netway.dongnehankki.user.exception.InvalidRefreshTokenException;
import org.netway.dongnehankki.user.exception.UnregisteredUserException;
import org.netway.dongnehankki.user.dto.response.UserResponse;
import org.netway.dongnehankki.user.dto.request.LoginRequest;
import org.netway.dongnehankki.user.dto.request.LoginResponse;
import org.netway.dongnehankki.user.dto.request.CustomerSignUpRequest;
import org.netway.dongnehankki.user.dto.request.OwnerSignUpRequest;
import org.netway.dongnehankki.user.domain.User;
import org.netway.dongnehankki.user.infrastructure.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;

import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.store.infrastructure.repository.StoreRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StoreRepository storeRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final S3Service s3Service;
    private final ReviewRepository reviewRepository;

    public UserResponse customerSignUp(CustomerSignUpRequest customerSignUpRequest){
        userRepository.findByLoginId(customerSignUpRequest.getLoginId()).ifPresent(it -> {
            throw new DuplicateLoginIdException();
        });

        String encodedPassword = passwordEncoder.encode(customerSignUpRequest.getPassword());
        User user = userRepository.save(User.ofCustomer(customerSignUpRequest.getLoginId(), encodedPassword, customerSignUpRequest.getNickname(), customerSignUpRequest.getName(), customerSignUpRequest.getPhoneNumber(), customerSignUpRequest.getBirth()));

        return UserResponse.fromEntity(user);
    }

    public UserResponse ownerSignUp(OwnerSignUpRequest ownerSignUpRequest) {
        userRepository.findByLoginId(ownerSignUpRequest.getLoginId()).ifPresent(it -> {
            throw new DuplicateLoginIdException();
        });

        Store store = storeRepository.findByStoreId(ownerSignUpRequest.getStoreId())
                .orElseThrow(() -> new UnregisteredStoreException());

        String encodedPassword = passwordEncoder.encode(ownerSignUpRequest.getPassword());
        User user = userRepository.save(User.ofOwner(ownerSignUpRequest.getLoginId(), encodedPassword, store.getName(), ownerSignUpRequest.getName(), ownerSignUpRequest.getPhoneNumber(), store,ownerSignUpRequest.getBirth()));

        return UserResponse.fromEntity(user);
    }


    public LoginResponse login(LoginRequest loginRequest){
        User user = userRepository.findByLoginId(loginRequest.getLoginId())
                .orElseThrow(UnregisteredUserException::new);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getLoginId(), loginRequest.getPassword());

        Authentication authentication;
        try {
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new InvalidPasswordException();
        }

        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());

        refreshTokenRepository.save(RefreshToken.builder()
                .userId(user.getUserId())
                .token(refreshToken)
                .expiration(jwtTokenProvider.getRefreshTokenExpirationMinutes() * 60)
                .build());

        return new LoginResponse(accessToken, refreshToken);
    }

    public LoginResponse reissueTokens(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        RefreshToken storedRefreshToken = refreshTokenRepository.findById(userId)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (!storedRefreshToken.getToken().equals(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        refreshTokenRepository.delete(storedRefreshToken);

        User user = userRepository.findById(userId)
                .orElseThrow(UnregisteredUserException::new);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(user),
                "",
                new CustomUserDetails(user).getAuthorities()
        );

        String newAccessToken = jwtTokenProvider.generateToken(authentication);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);

        refreshTokenRepository.save(RefreshToken.builder()
                .userId(userId)
                .token(newRefreshToken)
                .expiration(jwtTokenProvider.getRefreshTokenExpirationMinutes() * 60)
                .build());

        return new LoginResponse(newAccessToken, newRefreshToken);
    }


    public UserResponse findByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UnregisteredUserException());
        return UserResponse.fromEntity(user);
    }

    public UserResponse updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UnregisteredUserException());

        if(updateUserRequest.getNickname() != null){
            userRepository.findByNickname(updateUserRequest.getNickname()).ifPresent(foundUser -> {
                if (!foundUser.getUserId().equals(user.getUserId())) {
                    throw new DuplicateNickNameException();
                }
            });
            user.updateNickname(updateUserRequest.getNickname());
        }

        if(updateUserRequest.getPassword() != null){
            user.updatePassword(passwordEncoder.encode(updateUserRequest.getPassword()));
        }

        User savedUser = userRepository.save(user);

        return UserResponse.fromEntity(savedUser);
    }

    public boolean checkLoginId(String loginId) {
        return userRepository.findByLoginId(loginId).isEmpty();
    }

    public Boolean checkNickname(String nickname) {
        return userRepository.findByNickname(nickname).isEmpty();
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UnregisteredUserException());

        List<Review> reviews = reviewRepository.findAllByUser(user);
        reviews.forEach(Review::markAsDeleted);

        user.markAsDeleted();
    }

    @Transactional
	public void updateFcmToken(Long userId, FCMTokenRequest fcmTokenRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UnregisteredUserException());
        user.updateFcmToken(fcmTokenRequest.getToken());
	}

    @Transactional
    public void updateProfileImage(Long userId, MultipartFile profileImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(UnregisteredUserException::new);

        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isBlank()) {
            s3Service.deleteFile(user.getProfileImageUrl());
        }

        String profileImageUrl = s3Service.uploadFile(profileImage, "profile-images");

        user.updateProfileImage(profileImageUrl);
    }
}
