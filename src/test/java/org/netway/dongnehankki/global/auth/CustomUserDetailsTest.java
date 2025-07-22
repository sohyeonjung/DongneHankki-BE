package org.netway.dongnehankki.global.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netway.dongnehankki.user.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class CustomUserDetailsTest {

    private CustomUserDetails customUserDetails;
    private User user;

    @BeforeEach
    void setUp() {
        // 테스트를 위한 User 객체 생성
        user = User.ofCustomer("testLoginId", "testPassword", "testNickname", "testName", "010-1234-5678");
        customUserDetails = new CustomUserDetails(user);
    }

    @Test
    void getAuthorities_는_유저의_Role을_반환한다() {
        // when
        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();

        // then
        assertThat(authorities).hasSize(1);
        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly(User.Role.CUSTOMER.name());
    }

    @Test
    void getPassword_는_유저의_Password를_반환한다() {
        // when
        String password = customUserDetails.getPassword();

        // then
        assertThat(password).isEqualTo(user.getPassword());
    }

    @Test
    void getUsername_은_유저의_LoginId를_반환한다() {
        // when
        String username = customUserDetails.getUsername();

        // then
        assertThat(username).isEqualTo(user.getLoginId());
    }

    @Test
    void isAccountNonExpired_는_항상_true를_반환한다() {
        // when & then
        assertThat(customUserDetails.isAccountNonExpired()).isTrue();
    }

    @Test
    void isAccountNonLocked_는_항상_true를_반환한다() {
        // when & then
        assertThat(customUserDetails.isAccountNonLocked()).isTrue();
    }

    @Test
    void isCredentialsNonExpired_는_항상_true를_반환한다() {
        // when & then
        assertThat(customUserDetails.isCredentialsNonExpired()).isTrue();
    }

    @Test
    void isEnabled_는_항상_true를_반환한다() {
        // when & then
        assertThat(customUserDetails.isEnabled()).isTrue();
    }
}
