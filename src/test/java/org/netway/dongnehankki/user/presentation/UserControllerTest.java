package org.netway.dongnehankki.user.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.netway.dongnehankki.user.domain.User;
import org.netway.dongnehankki.user.domain.User.Role;
import org.netway.dongnehankki.user.dto.request.UpdateUserRequest;
import org.netway.dongnehankki.user.exception.DuplicateNickNameException;
import org.netway.dongnehankki.user.exception.EmptyNickNameException;
import org.netway.dongnehankki.user.exception.InvalidPasswordException;
import org.netway.dongnehankki.user.exception.UnregisteredUserException;
import org.netway.dongnehankki.user.application.UserService;
import org.netway.dongnehankki.user.dto.request.LoginResponse;
import org.netway.dongnehankki.user.dto.response.UserResponse;
import org.netway.dongnehankki.user.dto.request.LoginRequest;
import org.netway.dongnehankki.user.dto.request.CustomerSignUpRequest;
import org.netway.dongnehankki.user.dto.request.OwnerSignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;


    @Test
    public void 일반회원_회원가입() throws Exception{
        //given
        String loginId = "loginId";
        String password = "password";
        String nickname = "nickname";
        String name = "name";
        String phoneNumber = "010-1111-1111";

        //when
        when(userService.customerSignUp(any(CustomerSignUpRequest.class))).thenReturn(mock(
            UserResponse.class));

        //then
        mockMvc.perform(post("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(new CustomerSignUpRequest(loginId, password,nickname, name, phoneNumber)))
            .with(csrf())
        ).andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void 사장회원_회원가입() throws Exception{
        //given
        String loginId = "loginId";
        String password = "password";
        String nickname = "nickname";
        String name = "name";
        String phoneNumber = "010-1111-1111";
        Long storeId = 1L;

        //when
        when(userService.ownerSignUp(any(OwnerSignUpRequest.class))).thenReturn(mock(
            UserResponse.class));

        //then
        mockMvc.perform(post("/api/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new OwnerSignUpRequest(loginId,password,nickname,name, phoneNumber,storeId)))
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isOk());
    }
    @Test
    public void 회원가입시_loginId_중복체크_중복없을경우() throws Exception {
        //given
        String loginId = "loginId";

        //when
        when(userService.checkLoginId(any(String.class))).thenReturn(true);

        //then
        mockMvc.perform(get("/api/users/check/loginId")
                .param("loginId", loginId)
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.code").value("200"))
            .andExpect(jsonPath("$.message").value("사용 가능합니다."))
            .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    public void 회원가입시_loginId_중복체크_중복있을경우() throws Exception {
        //given
        String loginId = "existingLoginId";

        //when
        when(userService.checkLoginId(any(String.class))).thenReturn(false);

        //then
        mockMvc.perform(get("/api/users/check/loginId")
                .param("loginId", loginId)
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.code").value("200"))
            .andExpect(jsonPath("$.message").value("이미 사용 중입니다."))
            .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    public void 회원가입시_nickName_중복체크_중복없을경우() throws Exception {
        //given
        String nickname = "nickname";

        //when
        when(userService.checkNickname(any(String.class))).thenReturn(true);

        //then
        mockMvc.perform(get("/api/users/check/nickname")
                .param("nickname", nickname)
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.code").value("200"))
            .andExpect(jsonPath("$.message").value("사용 가능합니다."))
            .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    public void 회원가입시_nickName_중복체크_중복있을경우() throws Exception {
        //given
        String nickname = "nickname";

        //when
        when(userService.checkNickname(any(String.class))).thenReturn(false);

        //then
        mockMvc.perform(get("/api/users/check/nickname")
                .param("nickname", nickname)
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.code").value("200"))
            .andExpect(jsonPath("$.message").value("이미 사용 중입니다."))
            .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    public void 회원가입시_이미_회원가입된_nickName으로_회원가입을_하는경우_에러반환() throws Exception{
        //given
        String loginId = "loginId";
        String password = "password";
        String nickname = "nickname";
        String name = "name";
        String phoneNumber = "010-1111-1111";

        //when
        when(userService.customerSignUp(any(CustomerSignUpRequest.class))).thenThrow(new DuplicateNickNameException());

        //then
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new CustomerSignUpRequest(loginId, password,nickname, name, phoneNumber)))
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void 로그인() throws Exception{
        //given
        String loginId = "loginId";
        String password = "password";

        //when
        when(userService.login(any(LoginRequest.class))).thenReturn(mock(LoginResponse.class));

        //then
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new LoginRequest(loginId, password)))
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void 로그인시_회원가입이_안된_id를_입력할경우_에러반환() throws Exception{
        //given
        String loginId = "loginId";
        String password = "password";

        //when
        when(userService.login(any(LoginRequest.class))).thenThrow(new UnregisteredUserException());

        //then
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new LoginRequest(loginId, password)))
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void 로그인시_틀린_PW를_입력할경우_에러반환() throws Exception{
        //given
        String loginId = "loginId";
        String password = "password";

        //when
        when(userService.login(any(LoginRequest.class))).thenThrow(new InvalidPasswordException());

        //then
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new LoginRequest(loginId, password)))
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void 단일_고객_회원_조회() throws Exception{
        //given
        Long userId = 1L;
        UserResponse mockUserResponse = new UserResponse(userId, "testLoginId", "testNickname","testName", "010-1111-1111", User.Role.CUSTOMER, null);

        //when
        when(userService.findByUserId(any(Long.class))).thenReturn(mockUserResponse);

        //then
        mockMvc.perform(get("/api/users/{userId}", userId)
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void 단일_점주_회원_조회() throws Exception{
        //given
        Long userId = 1L;
        String loginId = "ownerId";
        String nickname = "점주닉네임";
        String name = "name";
        String phoneNumber = "010-1111-1111";
        Long storeId = 100L;
        UserResponse mockUserResponse = new UserResponse(userId, loginId, nickname, name, phoneNumber ,Role.OWNER, storeId);

        //when
        when(userService.findByUserId(any(Long.class))).thenReturn(mockUserResponse);

        //then
        mockMvc.perform(get("/api/users/{userId}", userId)
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void 없는_회원_조회시_에러반환() throws Exception{
        //given
        Long userId = 1L;

        //when
        when(userService.findByUserId(any(Long.class))).thenThrow(new UnregisteredUserException());

        //then
        mockMvc.perform(get("/api/users/{userId}", userId)
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void 고객_회원_수정() throws Exception{
        //given
        Long userId = 1L;
        String updatedNickname = "새로운닉네임";
        String newPassword = "newPassword";
        String newName = "name";
        String newPhoneNumber = "010-1111-1111";
        UpdateUserRequest userUpdateRequest = new UpdateUserRequest(newPassword, updatedNickname);
        UserResponse mockUpdatedUserResponse = new UserResponse(userId, "testId", updatedNickname, newName, newPhoneNumber, Role.CUSTOMER, null);

        //when
        when(userService.updateUser(any(Long.class), any(UpdateUserRequest.class))).thenReturn(mockUpdatedUserResponse);

        //then
        mockMvc.perform(patch("/api/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userUpdateRequest))
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void 점주_회원_수정() throws Exception{
        //given
        Long userId = 1L;
        String updatedNickname = "새로운닉네임";
        String newPassword = "newPassword";
        UpdateUserRequest userUpdateRequest = new UpdateUserRequest(newPassword, updatedNickname);
        UserResponse mockUpdatedUserResponse = new UserResponse(userId, "testId", updatedNickname, "testName", "010-1111-1111", Role.OWNER, 1L);

        //when
        when(userService.updateUser(any(Long.class), any(UpdateUserRequest.class))).thenReturn(mockUpdatedUserResponse);

        //then
        mockMvc.perform(patch("/api/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userUpdateRequest))
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void 회원_수정시_빈_닉네임_입력시_에러반환() throws Exception {
        //given
        Long userId = 1L;
        String updatedNickname = "";
        UpdateUserRequest userUpdateRequest = new UpdateUserRequest(null, updatedNickname);

        //when
        when(userService.updateUser(any(Long.class), any(UpdateUserRequest.class))).thenThrow(new EmptyNickNameException());

        //then
        mockMvc.perform(patch("/api/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userUpdateRequest))
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void 인증되지_않은_회원이_수정시_에러반환() throws Exception {
        //given
        Long userId = 1L;
        UpdateUserRequest userUpdateRequest = new UpdateUserRequest("password", "nickname");

        //when & then
        mockMvc.perform(patch("/api/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userUpdateRequest))
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void 등록되지_않은_회원_수정시_에러반환() throws Exception {
        //given
        Long userId = 1L;
        String updatedNickname = "nickname";
        UpdateUserRequest userUpdateRequest = new UpdateUserRequest(null, updatedNickname);

        //when
        when(userService.updateUser(any(Long.class), any(UpdateUserRequest.class))).thenThrow(new UnregisteredUserException());

        //then
        mockMvc.perform(patch("/api/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userUpdateRequest))
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void 유저_softDelete_성공() throws Exception{
        // given
        Long userId = 1L;

        // when & then
        mockMvc.perform(delete("/api/users/{userId}", userId)
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    @WithMockUser
    public void 미등록유저_softDelete시_에러반환() throws Exception{
        // given
        Long userId = 1L;

        // when
        doThrow(new UnregisteredUserException()).when(userService).deleteUser(any(Long.class));

        //then
        mockMvc.perform(delete("/api/users/{userId}", userId)
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isUnauthorized());
    }







}
