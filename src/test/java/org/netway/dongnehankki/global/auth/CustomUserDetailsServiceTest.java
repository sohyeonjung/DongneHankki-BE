package org.netway.dongnehankki.global.auth;

import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.netway.dongnehankki.user.domain.User;
import org.netway.dongnehankki.user.exception.UnregisteredUserException;
import org.netway.dongnehankki.user.infrastructure.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.ofCustomer("testLoginId", "testPassword", "testNickname", "testName", "010-1234-5678",
            LocalDate.of(2025,8,22));
    }

    @Test
    void loadUserByUsername_유저가_존재하면_UserDetails를_반환한다() {
        // given
        String loginId = user.getLoginId();
        given(userRepository.findByLoginId(loginId)).willReturn(Optional.of(user));

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginId);

        // then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(loginId);
        assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    void loadUserByUsername_유저가_존재하지_않으면_예외를_던진다() {
        // given
        String nonExistentLoginId = "nonExistentUser";
        given(userRepository.findByLoginId(nonExistentLoginId)).willReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(UnregisteredUserException.class, () -> {
            customUserDetailsService.loadUserByUsername(nonExistentLoginId);
        });
    }
}
