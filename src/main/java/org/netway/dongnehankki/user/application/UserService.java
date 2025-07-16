package org.netway.dongnehankki.user.application;

import lombok.RequiredArgsConstructor;
import org.netway.dongnehankki.global.auth.CustomUserDetails;
import org.netway.dongnehankki.global.auth.jwt.JwtTokenProvider;
import org.netway.dongnehankki.global.auth.jwt.RefreshToken;
import org.netway.dongnehankki.global.auth.jwt.RefreshTokenRepository;
import org.netway.dongnehankki.user.dto.request.UpdateUserRequest;
import org.netway.dongnehankki.user.exception.DuplicateUserNameException;
import org.netway.dongnehankki.user.exception.InvalidPasswordException;
import org.netway.dongnehankki.global.exception.store.UnregisteredStoreException;
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
import org.netway.dongnehankki.store.infrastructure.StoreRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private  final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StoreRepository storeRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserResponse customerSignUp(CustomerSignUpRequest customerSignUpRequest){
        userRepository.findById(customerSignUpRequest.getId()).ifPresent(it -> {
            throw new DuplicateUserNameException();
        });

        String encodedPassword = passwordEncoder.encode(customerSignUpRequest.getPassword());
        User user = userRepository.save(User.ofCustomer(customerSignUpRequest.getId(), encodedPassword, customerSignUpRequest.getNickname()));

        return UserResponse.fromEntity(user);
    }

    public UserResponse ownerSignUp(OwnerSignUpRequest ownerSignUpRequest) {
        userRepository.findById(ownerSignUpRequest.getId()).ifPresent(it -> {
            throw new DuplicateUserNameException();
        });

        Store store = storeRepository.findByStoreId(ownerSignUpRequest.getStoreId())
                .orElseThrow(() -> new UnregisteredStoreException());

        String encodedPassword = passwordEncoder.encode(ownerSignUpRequest.getPassword());
        User user = userRepository.save(User.ofOwner(ownerSignUpRequest.getId(), encodedPassword, ownerSignUpRequest.getNickname(), store));

        return UserResponse.fromEntity(user);
    }


    public LoginResponse login(LoginRequest loginRequest){
        User user = userRepository.findById(loginRequest.getId())
                .orElseThrow(UnregisteredUserException::new);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getId(), loginRequest.getPassword());

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
        //TODO : Implementation
        return new UserResponse(userId, "updatedId", updateUserRequest.getNickname(), User.Role.CUSTOMER, null);
    }
}
