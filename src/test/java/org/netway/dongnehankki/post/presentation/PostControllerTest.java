import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.netway.dongnehankki.post.dto.request.PostCreateRequest;
import org.netway.dongnehankki.post.application.PostService;
import org.netway.dongnehankki.user.domain.User;
import org.netway.dongnehankki.global.auth.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostService postService;

    @Test
    @DisplayName("게시글 생성 API 호출 테스트")
    void testCreatePost() throws Exception {
        // Given
        MockMultipartFile imageFile = new MockMultipartFile(
            "images",
            "test.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        PostCreateRequest request = new PostCreateRequest(1L, "Test content", Collections.singletonList(imageFile), Collections.singletonList("tag1"));

        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);
        User mockUser = mock(User.class);
        when(mockUserDetails.getUser()).thenReturn(mockUser);
        when(mockUser.getUserId()).thenReturn(1L);
        when(mockUserDetails.getAuthorities()).thenReturn(Collections.emptyList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(mockUserDetails, null, mockUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // When & Then
        mockMvc.perform(multipart("/api/post/create")
                .file(imageFile)
                .param("storeId", String.valueOf(request.getStoreId()))
                .param("content", request.getContent())
                .param("hashtags", request.getHashtags().toArray(new String[0]))
                .with(csrf()))
            .andExpect(status().isOk());

        verify(postService).createPost(any(PostCreateRequest.class), eq(1L));
    }
}
