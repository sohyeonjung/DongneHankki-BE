package org.netway.dongnehankki.user.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.netway.dongnehankki.global.auth.jwt.JwtTokenProvider;
import org.netway.dongnehankki.global.auth.jwt.RefreshToken;
import org.netway.dongnehankki.global.auth.jwt.RefreshTokenRepository;
import org.netway.dongnehankki.store.application.StoreSyncService;
import org.netway.dongnehankki.store.exception.UnregisteredStoreException;
import org.netway.dongnehankki.user.dto.request.UpdateUserRequest;
import org.netway.dongnehankki.user.dto.response.UserResponse;
import org.netway.dongnehankki.user.exception.DuplicateNickNameException;
import org.netway.dongnehankki.user.exception.DuplicateLoginIdException;
import org.netway.dongnehankki.user.exception.InvalidPasswordException;
import org.netway.dongnehankki.user.exception.InvalidRefreshTokenException;
import org.netway.dongnehankki.user.exception.UnregisteredUserException;
import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.store.infrastructure.repository.StoreRepository;
import org.netway.dongnehankki.user.dto.request.LoginRequest;
import org.netway.dongnehankki.user.dto.request.LoginResponse;
import org.netway.dongnehankki.user.dto.request.CustomerSignUpRequest;
import org.netway.dongnehankki.user.dto.request.OwnerSignUpRequest;
import org.netway.dongnehankki.user.domain.User;
import org.netway.dongnehankki.user.fixture.CustomerUserFixture;
import org.netway.dongnehankki.user.fixture.OwnerUserFixture;
import org.netway.dongnehankki.user.infrastructure.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")

public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private StoreRepository storeRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;

    @MockitoBean
    private StoreSyncService storeSyncService;

    @Test
    void 일반회원_회원가입이_정상적으로_동작하는경우() {
        // given
        String loginId = "id";
        String password = "password";
        String nickname = "nickname";
        String name = "홍길동";
        String phoneNumber = "010-1234-5678";
        LocalDate birth =  LocalDate.of(2025,8,22);

        when(userRepository.findByLoginId(loginId)).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(CustomerUserFixture.get(loginId, password,nickname, name, phoneNumber, birth));
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        // when & then
        Assertions.assertDoesNotThrow(() -> userService.customerSignUp(new CustomerSignUpRequest(loginId,password,nickname,name,phoneNumber,birth)));
    }

    @Test
    void 사장회원_회원가입이_정상적으로_동작하는경우() {
        // given
        String loginId = "id";
        String password = "password";
        String nickname = "nickname";
        String name = "김사장";
        String phoneNumber = "010-9876-5432";
        Long storeId = 1L;
        LocalDate birth = LocalDate.of(2025,8,22);
        Store mockStore = mock(Store.class);

        when(userRepository.findByLoginId(loginId)).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(OwnerUserFixture.get(loginId, password, name, phoneNumber, mockStore, birth));
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(mockStore.getStoreId()).thenReturn(storeId);
        when(storeRepository.findByStoreId(storeId)).thenReturn(Optional.of(mockStore));

        // when & then
        Assertions.assertDoesNotThrow(() -> userService.ownerSignUp(new OwnerSignUpRequest(loginId,password,name,phoneNumber,storeId,birth)));
    }

    @Test
    void 사장회원_회원가입이_등록되지않은_storeId로_가입하는경우_익셉션() {
        // given
        String loginId = "id";
        String password = "password";
        String nickname = "nickname";
        String name = "김사장";
        String phoneNumber = "010-9876-5432";
        Long storeId = 1L;
        LocalDate birth = LocalDate.of(2025,8,22);
        Store mockStore = mock(Store.class);

        when(userRepository.findByLoginId(loginId)).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(OwnerUserFixture.get(loginId, password, name, phoneNumber, mockStore, birth));
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(storeRepository.findByStoreId(storeId)).thenReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(UnregisteredStoreException.class,() -> userService.ownerSignUp(new OwnerSignUpRequest(loginId,password,name,phoneNumber,storeId,birth)));
    }

    @Test
    void 회원가입시_loginId_중복체크에서_중복이_없을경우() {
        // given
        String loginId = "existingId";

        when(userRepository.findByLoginId(loginId)).thenReturn(Optional.empty());

        // when
        boolean isAvailable = userService.checkLoginId(loginId);

        // then
        assertThat(isAvailable).isTrue();
    }

    @Test
    void 회원가입시_loginId_중복체크에서_중복이_있을경우() {
        // given
        String loginId = "existingId";
        User existingUser = CustomerUserFixture.get(loginId, "password", "nickname", "name", "010-1111-1111", LocalDate.of(2025,8,22));

        when(userRepository.findByLoginId(loginId)).thenReturn(Optional.of(existingUser));

        // when
        boolean isAvailable = userService.checkLoginId(loginId);

        // then
        assertThat(isAvailable).isFalse();
    }

    @Test
    void 회원가입시_nickname_중복체크에서_중복이_없을경우() {
        // given
        String nickname = "nickname";

        when(userRepository.findByNickname(nickname)).thenReturn(Optional.empty());

        // when
        boolean isAvailable = userService.checkNickname(nickname);

        // then
        assertThat(isAvailable).isTrue();
    }

    @Test
    void 회원가입시_nickname_중복체크에서_중복이_있을경우() {
        // given
        String nickname = "nickname";
        User existingUser = CustomerUserFixture.get("loginId", "password", nickname, "name", "010-1111-1111", LocalDate.of(2025,8,22));

        when(userRepository.findByNickname(nickname)).thenReturn(Optional.of(existingUser));

        // when
        boolean isAvailable = userService.checkNickname(nickname);

        // then
        assertThat(isAvailable).isFalse();
    }

    @Test
    void 일반회원_회원가입시_id가_이미_존재하는_경우() {
        // given
        String loginId = "id";
        String password = "password";
        String nickname = "nickname";
        String name = "홍길동";
        String phoneNumber = "010-1234-5678";
        LocalDate birth = LocalDate.of(2025,8,22);

        User fixture = CustomerUserFixture.get(loginId, password,nickname, name, phoneNumber,birth);

        when(userRepository.findByLoginId(loginId)).thenReturn(Optional.of(fixture));

        // when & then
        Assertions.assertThrows(DuplicateLoginIdException.class, () -> userService.customerSignUp(new CustomerSignUpRequest(loginId,password,nickname,name,phoneNumber,birth)));
    }

    @Test
    void 사장회원_회원가입시_id가_이미_존재하는_경우() {
        // given
        String loginId = "id";
        String password = "password";
        String nickname = "nickname";
        String name = "김사장";
        String phoneNumber = "010-9876-5432";
        Long storeId = 1L;
        LocalDate birth = LocalDate.of(2025,8,22);
        Store mockStore = mock(Store.class);

        User fixture = OwnerUserFixture.get(loginId, password, name, phoneNumber, mockStore,birth);

        when(userRepository.findByLoginId(loginId)).thenReturn(Optional.of(fixture));
        when(userRepository.save(any())).thenReturn(OwnerUserFixture.get(loginId, password, name, phoneNumber, mockStore,birth));
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(mockStore.getStoreId()).thenReturn(storeId);
        when(storeRepository.findByStoreId(storeId)).thenReturn(Optional.of(mockStore));

        // when & then
        Assertions.assertThrows(DuplicateLoginIdException.class, () -> userService.ownerSignUp(new OwnerSignUpRequest(loginId,password,name,phoneNumber,storeId,birth)));
    }

    @Test
    void 로그인이_정상적으로_동작하는_경우() {
        // given
        String loginId = "id";
        String password = "password";
        String name = "name";
        String nickname = "nickname";
        String phoneNumber = "010-1111-1111";
        LocalDate birth = LocalDate.of(2025,8,22);
        Long userId = 1L;

        User fixture = CustomerUserFixture.get(loginId, password,nickname, name, phoneNumber,birth);
        when(userRepository.findByLoginId(loginId)).thenReturn(Optional.of(fixture));

        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        when(jwtTokenProvider.generateToken(authentication)).thenReturn("dummy_access_token");
        when(jwtTokenProvider.generateRefreshToken(userId)).thenReturn("dummy_refresh_token");
        when(jwtTokenProvider.getRefreshTokenExpirationMinutes()).thenReturn(1440L); // 24시간

        // when & then
        Assertions.assertDoesNotThrow(() -> userService.login(new LoginRequest(loginId,password)));
    }

    @Test
    void 회원가입하지_않은_정보로_로그인하는_경우() {
        // given
        String loginId = "id";
        String password = "password";

        when(userRepository.findByLoginId(loginId)).thenReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(
            UnregisteredUserException.class, () -> userService.login(new LoginRequest(loginId,password)));
    }

    @Test
    void 로그인시_비밀번호가_틀린_경우() {
        // given
        String loginId = "id";
        String password = "password";
        String nickname = "nickname";
        String name = "name";
        String phoneNumber = "010-1111-1111";
        String wrongPassword = "wrong_password";
        LocalDate birth = LocalDate.of(2025,8,22);

        User fixture = CustomerUserFixture.get(loginId, password, nickname, name, phoneNumber,birth);
        when(userRepository.findByLoginId(loginId)).thenReturn(Optional.of(fixture));

        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException(""));

        // when & then
        Assertions.assertThrows(
            InvalidPasswordException.class, () -> userService.login(new LoginRequest(loginId, wrongPassword)));
    }

    @Test
    void 리프레시_토큰_재발급이_정상적으로_동작하는_경우() {
        // given
        Long userId = 1L;
        String oldRefreshToken = "old_refresh_token";
        String newAccessToken = "new_access_token";
        String newRefreshToken = "new_refresh_token";

        User userFixture = CustomerUserFixture.get("loginId", "password", "nickname", "name", "010-1111-1111", LocalDate.of(2025,8,22));

        RefreshToken storedRefreshToken = RefreshToken.builder()
                .userId(userId)
                .token(oldRefreshToken)
                .expiration(1440L * 60)
                .build();

        when(jwtTokenProvider.validateToken(oldRefreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken(oldRefreshToken)).thenReturn(userId);
        when(refreshTokenRepository.findById(userId)).thenReturn(Optional.of(storedRefreshToken));
        when(userRepository.findById(userId)).thenReturn(Optional.of(userFixture));
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn(newAccessToken);
        when(jwtTokenProvider.generateRefreshToken(userId)).thenReturn(newRefreshToken);
        when(jwtTokenProvider.getRefreshTokenExpirationMinutes()).thenReturn(1440L);

        // when
        LoginResponse response = userService.reissueTokens(oldRefreshToken);

        // then
        Assertions.assertEquals(newAccessToken, response.getAccessToken());
        Assertions.assertEquals(newRefreshToken, response.getRefreshToken());
    }

    @Test
    void 유효하지_않은_리프레시_토큰으로_재발급을_요청하는_경우() {
        // given
        String invalidRefreshToken = "invalid_refresh_token";

        // 시나리오 1: 토큰 유효성 검증 실패
        when(jwtTokenProvider.validateToken(invalidRefreshToken)).thenReturn(false);
        // when & then
        Assertions.assertThrows(InvalidRefreshTokenException.class, () -> userService.reissueTokens(invalidRefreshToken));

        // 시나리오 2: Redis에 토큰이 없는 경우
        when(jwtTokenProvider.validateToken(invalidRefreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken(invalidRefreshToken)).thenReturn(1L);
        when(refreshTokenRepository.findById(1L)).thenReturn(Optional.empty());
        // when & then
        Assertions.assertThrows(InvalidRefreshTokenException.class, () -> userService.reissueTokens(invalidRefreshToken));

        // 시나리오 3: Redis에 저장된 토큰과 요청된 토큰이 일치하지 않는 경우
        String mismatchedRefreshToken = "mismatched_refresh_token";
        RefreshToken storedRefreshToken = RefreshToken.builder()
                .userId(1L)
                .token("actual_stored_token")
                .expiration(1440L * 60)
                .build();
        when(jwtTokenProvider.validateToken(mismatchedRefreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken(mismatchedRefreshToken)).thenReturn(1L);
        when(refreshTokenRepository.findById(1L)).thenReturn(Optional.of(storedRefreshToken));
        // when & then
        Assertions.assertThrows(InvalidRefreshTokenException.class, () -> userService.reissueTokens(mismatchedRefreshToken));
    }

    @Test
    void 고객_회원_수정이_성공적으로_동작하는_경우() {

        // given
        User existingUser = User.ofCustomer("loginId", "oldPass", "oldNick" ,"oldName", "010-1111-1111", LocalDate.of(2025,8,22));
        // anyLong() 사용
        given(userRepository.findById(anyLong()))
            .willReturn(Optional.of(existingUser));
        given(userRepository.save(any(User.class)))
            .willAnswer(invocation -> invocation.getArgument(0));
        given(passwordEncoder.encode("newPass")).willReturn("newPass");

        UpdateUserRequest req = new UpdateUserRequest("newPass", "newNick");

        // when
        UserResponse resp = userService.updateUser(999L, req);

        // then
        assertThat(existingUser.getPassword()).isEqualTo("newPass");
        assertThat(existingUser.getNickname()).isEqualTo("newNick");
        assertThat(resp.getNickname()).isEqualTo("newNick");
    }

    @Test
    void 점주_회원_수정이_성공적으로_동작하는_경우() {

        // given
        Store mockStore = mock(Store.class);
        User existingUser = User.ofOwner("loginId", "oldPass", "oldNick", "oldName", "010-1111-1111", mockStore,LocalDate.of(2025,8,22));
        // anyLong() 사용
        given(userRepository.findById(anyLong()))
            .willReturn(Optional.of(existingUser));
        given(userRepository.save(any(User.class)))
            .willAnswer(invocation -> invocation.getArgument(0));
        given(passwordEncoder.encode("newPass")).willReturn("newPass");

        UpdateUserRequest req = new UpdateUserRequest("newPass", "newNick");

        // when
        UserResponse resp = userService.updateUser(999L, req);

        // then
        assertThat(existingUser.getPassword()).isEqualTo("newPass");
        assertThat(existingUser.getNickname()).isEqualTo("newNick");
        assertThat(existingUser.getStore()).isEqualTo(mockStore);
        assertThat(resp.getNickname()).isEqualTo("newNick");
    }

    @Test
    void 다른_유저가_사용중인_닉네임을_사용하는_경우() {
        // given
        User existingUser = User.ofCustomer("loginId", "oldPass", "oldNick", "oldName", "010-1111-1111",LocalDate.of(2025,8,22));
        try {
            Field field = User.class.getDeclaredField("userId");
            field.setAccessible(true);
            field.set(existingUser, 999L);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        User anotherUser = User.ofCustomer("anotherLoginId", "pass", "newNick", "newName", "010-2222-2222",LocalDate.of(2025,8,22));
        try {
            Field field = User.class.getDeclaredField("userId");
            field.setAccessible(true);
            field.set(anotherUser, 1000L);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        given(userRepository.findById(999L)).willReturn(Optional.of(existingUser));
        given(userRepository.findByNickname("newNick")).willReturn(Optional.of(anotherUser));
        given(passwordEncoder.encode("newPass")).willReturn("encodedNewPass");

        UpdateUserRequest req = new UpdateUserRequest("newPass", "newNick");

        // when & then
        Assertions.assertThrows(DuplicateNickNameException.class, () -> userService.updateUser(999L, req));
    }

    @Test
    void 유저가_자신의_닉네임으로_수정하는_경우_성공() {
        // given
        long userId = 999L;
        String nickname = "myNick";
        User existingUser = User.ofCustomer("loginId", "oldPass", nickname, "oldName", "010-1111-1111",LocalDate.of(2025,8,22));
        try {
            Field field = User.class.getDeclaredField("userId");
            field.setAccessible(true);
            field.set(existingUser, userId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
        given(userRepository.findByNickname(nickname)).willReturn(Optional.of(existingUser));
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        UpdateUserRequest req = new UpdateUserRequest("newPass", nickname);

        // when & then
        Assertions.assertDoesNotThrow(() -> userService.updateUser(userId, req));
    }

    @Test
    void 고객_회원_닉네임만_수정이_성공적으로_동작하는_경우() {

        // given
        User existingUser = User.ofCustomer("loginId", "oldPass", "oldNick", "oldName", "010-1111-1111",LocalDate.of(2025,8,22));
        // anyLong() 사용
        given(userRepository.findById(anyLong()))
            .willReturn(Optional.of(existingUser));
        given(userRepository.save(any(User.class)))
            .willAnswer(invocation -> invocation.getArgument(0));

        UpdateUserRequest req = new UpdateUserRequest(null, "newNick");

        // when
        UserResponse resp = userService.updateUser(999L, req);

        // then
        assertThat(existingUser.getPassword()).isEqualTo("oldPass");
        assertThat(existingUser.getNickname()).isEqualTo("newNick");
        assertThat(resp.getNickname()).isEqualTo("newNick");
    }

    @Test
    void 고객_회원_패스워드만_수정이_성공적으로_동작하는_경우() {

        // given
        User existingUser = User.ofCustomer("loginId", "oldPass", "oldNick","oldName", "010-1111-1111",LocalDate.of(2025,8,22));
        // anyLong() 사용
        given(userRepository.findById(anyLong()))
            .willReturn(Optional.of(existingUser));
        given(userRepository.save(any(User.class)))
            .willAnswer(invocation -> invocation.getArgument(0));
        given(passwordEncoder.encode("newPass")).willReturn("newPass");

        UpdateUserRequest req = new UpdateUserRequest("newPass", null);

        // when
        UserResponse resp = userService.updateUser(999L, req);

        // then
        assertThat(existingUser.getPassword()).isEqualTo("newPass");
        assertThat(existingUser.getNickname()).isEqualTo("oldNick");
        assertThat(resp.getNickname()).isEqualTo("oldNick");
    }

    @Test
    void 고객_회원_패스워드만_수정하려고_할때_닉네임은_기존_닉네임을_입력하는_경우_성공적으로_동작하는_경우() {

        // given
        User existingUser = User.ofCustomer("loginId", "oldPass", "oldNick","oldName", "010-1111-1111",LocalDate.of(2025,8,22));
        // anyLong() 사용
        given(userRepository.findById(anyLong()))
            .willReturn(Optional.of(existingUser));
        given(userRepository.save(any(User.class)))
            .willAnswer(invocation -> invocation.getArgument(0));
        given(passwordEncoder.encode("newPass")).willReturn("newPass");

        UpdateUserRequest req = new UpdateUserRequest("newPass", "oldNick");

        // when
        UserResponse resp = userService.updateUser(999L, req);

        // then
        assertThat(existingUser.getPassword()).isEqualTo("newPass");
        assertThat(existingUser.getNickname()).isEqualTo("oldNick");
        assertThat(resp.getNickname()).isEqualTo("oldNick");
    }

    @Test
    void 유저_softDelete가_성공적으로_동작하는_경우(){
        // given
        long userId = 1L;
        User existingUser = User.ofCustomer("loginId", "oldPass", "oldNick","oldName", "010-1111-1111",LocalDate.of(2025,8,22));

        given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));

        // when
        userService.deleteUser(userId);

        // then
        assertThat(existingUser.getDeletedAt()).isNotNull();

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        Assertions.assertThrows(UnregisteredUserException.class, () -> {
            userService.findByUserId(userId);
        });
    }

    @Test
    void 가입하지않은유저_softDelete시_실패하는_경우(){
        // given
        given(userRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(UnregisteredUserException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void userId로_유저_정보_찾기_성공하는_경우(){
        // given
        long userId = 1L;
        User existingUser = User.ofCustomer("loginId", "oldPass", "oldNick","oldName", "010-1111-1111",LocalDate.of(2025,8,22));
        given(userRepository.findById(userId))
            .willReturn(Optional.of(existingUser));

        // when
        UserResponse resp = userService.findByUserId(1L);

        // then
        assertThat(resp.getNickname()).isEqualTo("oldNick");
    }

    @Test
    void 존재하지않는_userId로_유저_정보_찾기시_실패하는_경우(){
        // given
        long userId = 1L;
        given(userRepository.findById(userId))
            .willReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(UnregisteredUserException.class, () -> userService.findByUserId(1L));
    }

    @Test
    void 존재하지_않는_유저_정보_수정시_실패하는_경우() {
        // given
        long userId = 1L;
        UpdateUserRequest req = new UpdateUserRequest("newPass", "newNick");

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(UnregisteredUserException.class, () -> {
            userService.updateUser(userId, req);
        });
    }

    @Test
    void 리프레시_토큰_재발급시_존재하지_않는_유저인_경우_실패() {
        // given
        Long userId = 1L;
        String refreshToken = "valid_refresh_token";

        RefreshToken storedRefreshToken = RefreshToken.builder()
                .userId(userId)
                .token(refreshToken)
                .expiration(1440L * 60)
                .build();

        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken(refreshToken)).thenReturn(userId);
        when(refreshTokenRepository.findById(userId)).thenReturn(Optional.of(storedRefreshToken));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(UnregisteredUserException.class, () -> {
            userService.reissueTokens(refreshToken);
        });
    }
}
