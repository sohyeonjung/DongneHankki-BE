package org.netway.dongnehankki.post.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.netway.dongnehankki.global.util.S3Service;
import org.netway.dongnehankki.post.domain.Hashtag;
import org.netway.dongnehankki.post.domain.Image;
import org.netway.dongnehankki.post.domain.Post;
import org.netway.dongnehankki.post.domain.PostHashtag;
import org.netway.dongnehankki.post.dto.request.PostCreateRequest;
import org.netway.dongnehankki.post.dto.response.PostResponse;
import org.netway.dongnehankki.post.exception.PostNotFoundException;
import org.netway.dongnehankki.post.repository.HashtagRepository;
import org.netway.dongnehankki.post.repository.ImageRepository;
import org.netway.dongnehankki.post.repository.PostHashtagRepository;
import org.netway.dongnehankki.post.repository.PostRepository;
import org.netway.dongnehankki.store.domain.Store;
import org.netway.dongnehankki.store.infrastructure.repository.StoreRepository;
import org.netway.dongnehankki.user.domain.User;
import org.netway.dongnehankki.user.infrastructure.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private HashtagRepository hashtagRepository;
    @Mock
    private PostHashtagRepository postHashtagRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private S3Service s3Service;

    @Test
    @DisplayName("게시글 생성 테스트")
    void createPost_success() {
        // given
        Long userId = 1L;
        Long storeId = 1L;
        MultipartFile mockFile = mock(MultipartFile.class);
        PostCreateRequest request = new PostCreateRequest(storeId, "새 게시글", new MultipartFile[]{mockFile}, List.of("#맛집"));

        User user = User.ofCustomer("loginId", "password", "nickname", "name", "phone");
        Store store = Store.createStore("가게", 1.0, 1.0, "주소", "시군", 1, 1L);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
        given(s3Service.uploadFile(any(), anyString())).willReturn("s3-image-url");
        given(hashtagRepository.findByName(anyString())).willReturn(Optional.of(Hashtag.createHashtag("#맛집")));

        // when
        postService.createPost(request, userId);

        // then
        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository, times(1)).save(postArgumentCaptor.capture());
        Post savedPost = postArgumentCaptor.getValue();

        assertThat(savedPost.getImages()).hasSize(1);
        assertThat(savedPost.getImages().get(0).getUrl()).isEqualTo("s3-image-url");
        assertThat(savedPost.getPostHashtags()).hasSize(1);
        assertThat(savedPost.getPostHashtags().get(0).getHashtag().getName()).isEqualTo("#맛집");
    }

    @Test
    @DisplayName("단일 게시글 조회 성공 테스트")
    void getPost_success() {
        // given
        Long postId = 1L;
        User user = User.ofCustomer("loginId", "password", "nickname", "name", "phone");
        Store store = Store.createStore("가게", 1.0, 1.0, "주소", "시군", 1, 1L);
        Post post = Post.createPost("내용", store, user);

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when
        PostResponse response = postService.getPost(postId);

        // then
        assertThat(response.getContent()).isEqualTo("내용");
        assertThat(response.getStoreName()).isEqualTo("가게");
        assertThat(response.getUserNickname()).isEqualTo("nickname");
    }

    @Test
    @DisplayName("단일 게시글 조회 실패 - 게시글 없음")
    void getPost_throwsException_whenPostNotFound() {
        // given
        Long postId = 1L;
        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.getPost(postId))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessage("존재하지 않는 게시글 입니다.");
    }

    @Test
    @DisplayName("가게별 게시글 목록 조회 테스트")
    void getPostsByStore_success() {

    }
}
