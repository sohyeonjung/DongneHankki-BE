package org.netway.dongnehankki.post.application;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.netway.dongnehankki.post.domain.Post;
import org.netway.dongnehankki.post.dto.request.PostCreateRequest;
import org.netway.dongnehankki.post.repository.PostRepository;
import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.store.infrastructure.repository.StoreRepository;
import org.netway.dongnehankki.user.domain.User;
import org.netway.dongnehankki.user.infrastructure.UserRepository;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StoreRepository storeRepository; 

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("게시글 생성 시 Post 객체가 올바르게 저장된다")
    void createPost_savesPostEntity() {
        // Given
        Long userId = 1L;
        PostCreateRequest request = new PostCreateRequest(1L, "New Post", Collections.emptyList(), Collections.emptyList());
        User user = User.ofCustomer("loginId", "password", "nickname", "name", "phone"); // Dummy user

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(new Store()));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        postService.createPost(request, userId);

        // Then
        verify(userRepository).findById(userId);
        verify(storeRepository).findById(anyLong());
        verify(postRepository).save(any(Post.class));
    }
}