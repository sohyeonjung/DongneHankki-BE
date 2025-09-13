package org.netway.dongnehankki.post.application;

import java.util.List;
import org.netway.dongnehankki.post.domain.Post;
import org.netway.dongnehankki.post.domain.Post.Role;
import org.netway.dongnehankki.post.dto.request.PostCreateRequest;
import org.netway.dongnehankki.post.dto.request.PostUpdateRequest;
import org.netway.dongnehankki.post.dto.response.CursorResult;
import org.netway.dongnehankki.post.dto.response.PostResponse;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {
    void createPost(PostCreateRequest request, Long userId, Role role);
    PostResponse getPost(Long postId, Long userId);
    CursorResult<PostResponse> getPostsByStore(Long storeId, Long cursorPostId, int pageSize);
    CursorResult<PostResponse> getPostsByStoreAndRole(Long storeId, Role role, Long cursorPostId, int pageSize);
    void deletePost(Long postId, Long userId);
    void updatePost(Long postId, PostUpdateRequest request, Long userId);
    CursorResult<PostResponse> getPostsFromFollowedStores(Long userId, Long cursorPostId, int pageSize);
    CursorResult<PostResponse> latestPosts(Long cursorPostId, int pageSize);
    String generatePost(Long storeId, String text, MultipartFile image);
    List<Post> getRecommendedPosts(Long userId, int limit);
}
