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
import org.netway.dongnehankki.post.application.PostService;
import org.netway.dongnehankki.post.domain.Hashtag;
import org.netway.dongnehankki.post.domain.Post;
import org.netway.dongnehankki.post.dto.request.PostCreateRequest;
import org.netway.dongnehankki.post.repository.HashtagRepository;
import org.netway.dongnehankki.post.repository.ImageRepository;
import org.netway.dongnehankki.post.repository.PostHashtagRepository;
import org.netway.dongnehankki.post.repository.PostRepository;
import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.store.infrastructure.repository.StoreRepository;
import org.netway.dongnehankki.user.domain.User;
import org.netway.dongnehankki.user.infrastructure.UserRepository;
import org.netway.dongnehankki.global.util.S3Service;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private HashtagRepository hashtagRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StoreRepository storeRepository; 
    @Mock
    private PostHashtagRepository postHashtagRepository;
    @Mock
    private S3Service s3Service;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("게시글 생성 시 Post 객체가 올바르게 저장된다")
    void createPost_savesPostEntity() {
        // Given
        Long userId = 1L;
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        PostCreateRequest request = new PostCreateRequest(1L, "New Post", Arrays.asList(mockFile), Arrays.asList("tag1", "tag2"));
        User user = User.ofCustomer("loginId", "password", "nickname", "name", "phone"); // Dummy user

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(new Store()));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(s3Service.uploadFile(any(MultipartFile.class), anyString())).thenReturn("http://mock-s3-url/test.jpg");
        when(hashtagRepository.findByName("tag1")).thenReturn(Optional.empty());
        when(hashtagRepository.findByName("tag2")).thenReturn(Optional.of(Hashtag.of("tag2")));

        // When
        postService.createPost(request, userId);

        // Then
        verify(userRepository).findById(userId);
        verify(storeRepository).findById(anyLong());
        verify(postRepository).save(any(Post.class));
        verify(s3Service).uploadFile(any(MultipartFile.class), eq("post-images"));
        verify(imageRepository).save(any());
        verify(hashtagRepository, times(2)).findByName(anyString());
        verify(hashtagRepository).save(any(Hashtag.class));
        verify(postHashtagRepository, times(2)).save(any());
    }
}