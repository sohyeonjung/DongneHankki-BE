package org.netway.dongnehankki.post.presentation;

import lombok.RequiredArgsConstructor;
import org.netway.dongnehankki.global.auth.CustomUserDetails;
import org.netway.dongnehankki.global.common.ApiResponse;
import org.netway.dongnehankki.post.application.PostService;
import org.netway.dongnehankki.post.domain.Post;
import org.netway.dongnehankki.post.domain.Post.Role;
import org.netway.dongnehankki.post.dto.request.CommentRequest;
import org.netway.dongnehankki.post.dto.request.PostCreateRequest;
import org.netway.dongnehankki.post.dto.request.PostUpdateRequest;
import org.netway.dongnehankki.post.dto.response.CursorResult;
import org.netway.dongnehankki.post.dto.response.PostResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.netway.dongnehankki.post.application.CommentService;
import org.netway.dongnehankki.post.dto.response.CommentResponse;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    @PostMapping(value = "/owners", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<Void>> createOwnerPost(
        @ModelAttribute PostCreateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.createPost(request, userDetails.getUser().getUserId(), Post.Role.OWNER);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PostMapping(value = "/customers", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<Void>> createCustomerPost(
        @ModelAttribute PostCreateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.createPost(request, userDetails.getUser().getUserId(), Post.Role.CUSTOMER);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updatePost(
        @PathVariable Long postId,
        @RequestBody PostUpdateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.updatePost(postId, request, userDetails.getUser().getUserId());
        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(@PathVariable Long postId) {
        PostResponse response = postService.getPost(postId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/store/{storeId}/owners")
    public ResponseEntity<ApiResponse<CursorResult<PostResponse>>> getOwnerPostsByStore(
            @PathVariable Long storeId,
            @RequestParam(name = "cursorPostId", required = false) Long cursorPostId,
            @RequestParam(defaultValue = "10") int size) {
        CursorResult<PostResponse> response = postService.getPostsByStoreAndRole(storeId, Role.OWNER, cursorPostId, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/store/{storeId}/customers")
    public ResponseEntity<ApiResponse<CursorResult<PostResponse>>> getCustomerPostsByStore(
            @PathVariable Long storeId,
            @RequestParam(name = "cursorPostId", required = false) Long cursorPostId,
            @RequestParam(defaultValue = "10") int size) {
        CursorResult<PostResponse> response = postService.getPostsByStoreAndRole(storeId, Role.CUSTOMER, cursorPostId, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
        @PathVariable Long postId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.deletePost(postId, userDetails.getUser().getUserId());
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<Void>> createComment(
            @PathVariable Long postId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.createComment(postId, request, userDetails.getUser().getUserId());
        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(@PathVariable Long postId) {
        List<CommentResponse> comments = commentService.getComments(postId);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    @GetMapping("/followed")
    public ResponseEntity<ApiResponse<CursorResult<PostResponse>>> getPostsFromFollowedStores(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "cursorPostId", required = false) Long cursorPostId,
            @RequestParam(defaultValue = "10") int size) {
        CursorResult<PostResponse> response = postService.getPostsFromFollowedStores(userDetails.getUser().getUserId(), cursorPostId, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
