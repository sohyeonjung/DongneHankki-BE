package org.netway.dongnehankki.post.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.netway.dongnehankki.global.auth.CustomUserDetails;
import org.netway.dongnehankki.global.auth.jwt.JwtTokenProvider;
import org.netway.dongnehankki.post.application.PostService;
import org.netway.dongnehankki.post.dto.response.CursorResult;
import org.netway.dongnehankki.post.dto.response.PostResponse;
import org.netway.dongnehankki.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("게시글 생성 성공 테스트")

    void createPost_success() throws Exception {
        // given
        MockMultipartFile imageFile = new MockMultipartFile("images", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image".getBytes());

        User mockUser = mock(User.class);
        given(mockUser.getUserId()).willReturn(1L);
        given(mockUser.getRole()).willReturn(User.Role.CUSTOMER); // 이 줄을 추가합니다.
        CustomUserDetails mockUserDetails = new CustomUserDetails(mockUser);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(mockUserDetails, null, mockUserDetails.getAuthorities());

        // when & then
        mockMvc.perform(multipart("/api/posts/customers")
                        .file(imageFile)
                        .param("storeId", "1")
                        .param("content", "맛있어요!")
                        .param("hashtags", "#맛집", "#강추")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf())
                        // 2. 생성한 인증 정보를 요청에 명시적으로 추가
                        .with(authentication(authenticationToken)))
                .andExpect(status().isOk())
                .andDo(print());

        verify(postService).createPost(any(), eq(1L), eq(org.netway.dongnehankki.post.domain.Post.Role.CUSTOMER));
    }

    @Test
    @WithMockUser
    @DisplayName("단일 게시글 조회 성공 테스트")
    void getPost_success() throws Exception {
        // given
        long postId = 1L;
        PostResponse response = PostResponse.builder()
                .postId(postId)
                .content("게시글 내용")
                .storeId(1L)
                .storeName("가게이름")
                .userId(1L)
                .userNickname("작성자")
                .images(Collections.emptyList())
                .hashtags(Collections.emptyList())
                .createdAt(LocalDateTime.now())
                .build();

        given(postService.getPost(anyLong())).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/posts/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.postId").value(postId))
                .andExpect(jsonPath("$.data.content").value("게시글 내용"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("가게별 게시글 목록 조회 성공 테스트")
    void getPostsByStore_success() throws Exception {
        // given
        Long storeId = 1L;
        int size = 5;
        Long nextCursor = 5L; // 다음 페이지를 조회하기 위한 커서 ID

        List<PostResponse> postResponses = List.of(
                PostResponse.builder().postId(10L).content("게시글 10").build(),
                PostResponse.builder().postId(9L).content("게시글 9").build(),
                PostResponse.builder().postId(8L).content("게시글 8").build(),
                PostResponse.builder().postId(7L).content("게시글 7").build(),
                PostResponse.builder().postId(6L).content("게시글 6").build()
        );
        CursorResult<PostResponse> mockResult = new CursorResult<>(postResponses, nextCursor);

        given(postService.getPostsByStore(eq(storeId), any(), eq(size))).willReturn(mockResult);

        // when & then
        mockMvc.perform(get("/api/posts/store/{storeId}", storeId)
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.values").isArray())
                .andExpect(jsonPath("$.data.values.length()").value(size))
                .andExpect(jsonPath("$.data.values[0].postId").value(10L))
                .andExpect(jsonPath("$.data.nextCursor").value(nextCursor))
                .andDo(print());

        verify(postService).getPostsByStore(eq(storeId), any(), eq(size));
    }

    @Test
    @DisplayName("게시글 삭제 성공 테스트")
    void deletePost_success() throws Exception {
        // given
        Long postId = 1L;
        User mockUser = mock(User.class);
        given(mockUser.getUserId()).willReturn(1L);
        given(mockUser.getRole()).willReturn(User.Role.CUSTOMER);
        CustomUserDetails mockUserDetails = new CustomUserDetails(mockUser);
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(mockUserDetails, null, mockUserDetails.getAuthorities());

        // when & then
        mockMvc.perform(delete("/api/posts/{postId}", postId)
                .with(csrf())
                .with(authentication(authenticationToken)))
            .andExpect(status().isOk())
            .andDo(print());

        verify(postService).deletePost(eq(postId), eq(1L));
    }
}