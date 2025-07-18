package org.netway.dongnehankki.user.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
        String id = "username";
        String password = "password";
        String nickname = "nickname";

        when(userService.customerSignUp(any(CustomerSignUpRequest.class))).thenReturn(mock(
            UserResponse.class));

        mockMvc.perform(post("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(new CustomerSignUpRequest(id, password,nickname)))
            .with(csrf())
        ).andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void 사장회원_회원가입() throws Exception{
        String id = "username";
        String password = "password";
        String nickname = "nickname";
        Long storeId = 1L;

        mockMvc.perform(post("/api/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new OwnerSignUpRequest(id,password,nickname,storeId)))
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void 회원가입시_이미_회원가입된_userName으로_회원가입을_하는경우_에러반환() throws Exception{
        String id = "username";
        String password = "password";
        String nickname = "nickname";

        when(userService.customerSignUp(any(CustomerSignUpRequest.class))).thenThrow(new DuplicateNickNameException());

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new CustomerSignUpRequest(id, password,nickname)))
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void 로그인() throws Exception{
        String id = "username";
        String password = "password";

        when(userService.login(any(LoginRequest.class))).thenReturn(mock(LoginResponse.class));

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new LoginRequest(id, password)))
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void 로그인시_회원가입이_안된_id를_입력할경우_에러반환() throws Exception{
        String id = "username";
        String password = "password";

        when(userService.login(any(LoginRequest.class))).thenThrow(new UnregisteredUserException());

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new LoginRequest(id, password)))
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void 로그인시_틀린_PW를_입력할경우_에러반환() throws Exception{
        String id = "username";
        String password = "password";

        when(userService.login(any(LoginRequest.class))).thenThrow(new InvalidPasswordException());

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new LoginRequest(id, password)))
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void 단일_고객_회원_조회() throws Exception{

        Long userId = 1L;
        UserResponse mockUserResponse = new UserResponse(userId, "testId", "testNickname", User.Role.CUSTOMER, null);
        when(userService.findByUserId(any(Long.class))).thenReturn(mockUserResponse);

        mockMvc.perform(get("/api/users/{userId}", userId)
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void 단일_점주_회원_조회() throws Exception{

        Long userId = 1L;
        String id = "ownerId";
        String nickname = "점주닉네임";
        Long storeId = 100L;

        UserResponse mockUserResponse = new UserResponse(userId, id, nickname, Role.OWNER, storeId);
        when(userService.findByUserId(any(Long.class))).thenReturn(mockUserResponse);

        mockMvc.perform(get("/api/users/{userId}", userId)
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void 없는_회원_조회시_에러반환() throws Exception{

        Long userId = 1L;
        when(userService.findByUserId(any(Long.class))).thenThrow(new UnregisteredUserException());

        mockMvc.perform(get("/api/users/{userId}", userId)
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void 고객_회원_수정() throws Exception{

        Long userId = 1L;
        String updatedNickname = "새로운닉네임";
        String newPassword = "newPassword";
        UpdateUserRequest userUpdateRequest = new UpdateUserRequest(newPassword, updatedNickname);

        UserResponse mockUpdatedUserResponse = new UserResponse(userId, "testId", updatedNickname, Role.CUSTOMER, null);
        when(userService.updateUser(any(Long.class), any(UpdateUserRequest.class))).thenReturn(mockUpdatedUserResponse);

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

        Long userId = 1L;
        String updatedNickname = "새로운닉네임";
        String newPassword = "newPassword";
        UpdateUserRequest userUpdateRequest = new UpdateUserRequest(newPassword, updatedNickname);

        UserResponse mockUpdatedUserResponse = new UserResponse(userId, "testId", updatedNickname, Role.OWNER, 1L);
        when(userService.updateUser(any(Long.class), any(UpdateUserRequest.class))).thenReturn(mockUpdatedUserResponse);

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
        Long userId = 1L;
        String updatedNickname = "";
        UpdateUserRequest userUpdateRequest = new UpdateUserRequest(null, updatedNickname);

        when(userService.updateUser(any(Long.class), any(UpdateUserRequest.class))).thenThrow(new EmptyNickNameException());

        mockMvc.perform(patch("/api/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userUpdateRequest))
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void 인증되지_않은_회원이_수정시_에러반환() throws Exception {
        Long userId = 1L;
        UpdateUserRequest userUpdateRequest = new UpdateUserRequest("password", "nickname");

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
        Long userId = 1L;
        String updatedNickname = "nickname";
        UpdateUserRequest userUpdateRequest = new UpdateUserRequest(null, updatedNickname);

        when(userService.updateUser(any(Long.class), any(UpdateUserRequest.class))).thenThrow(new UnregisteredUserException());

        mockMvc.perform(patch("/api/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userUpdateRequest))
                .with(csrf())
            ).andDo(print())
            .andExpect(status().isUnauthorized());
    }






}
