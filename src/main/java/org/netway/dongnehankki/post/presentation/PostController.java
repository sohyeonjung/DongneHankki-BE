package org.netway.dongnehankki.post.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.netway.dongnehankki.global.auth.CustomUserDetails;
import org.netway.dongnehankki.global.common.ApiResponse;
import org.netway.dongnehankki.post.application.CommentService;
import org.netway.dongnehankki.post.application.PostService;
import org.netway.dongnehankki.post.domain.Post;
import org.netway.dongnehankki.post.domain.Post.Role;
import org.netway.dongnehankki.post.dto.request.CommentRequest;
import org.netway.dongnehankki.post.dto.request.PostCreateRequest;
import org.netway.dongnehankki.post.dto.request.PostUpdateRequest;
import org.netway.dongnehankki.post.dto.response.CommentResponse;
import org.netway.dongnehankki.post.dto.response.CursorResult;
import org.netway.dongnehankki.post.dto.response.PostResponse;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "게시글", description = "게시글 관련 API")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    @Operation(summary = "사장님 게시글 작성", description = "사장님이 게시글을 작성합니다. (form-data)")
    @SecurityRequirement(name = "bearer-key")
    @PostMapping(value = "/owners", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<Void>> createOwnerPost(
        @ModelAttribute PostCreateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.createPost(request, userDetails.getUser().getUserId(), Post.Role.OWNER);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "일반 유저 게시글 작성", description = "일반 유저가 게시글을 작성합니다. (form-data)")
    @SecurityRequirement(name = "bearer-key")
    @PostMapping(value = "/customers", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<Void>> createCustomerPost(
        @ModelAttribute PostCreateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.createPost(request, userDetails.getUser().getUserId(), Post.Role.CUSTOMER);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
    @SecurityRequirement(name = "bearer-key")
    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updatePost(
        @PathVariable Long postId,
        @RequestBody PostUpdateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.updatePost(postId, request, userDetails.getUser().getUserId());
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "게시글 단건 조회", description = "게시글 ID로 특정 게시글을 조회합니다.")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(@PathVariable Long postId) {
        PostResponse response = postService.getPost(postId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "가게별 사장님 게시글 조회", description = "특정 가게의 사장님 게시글을 커서 기반 페이징으로 조회합니다.")
    @GetMapping("/store/{storeId}/owners")
    public ResponseEntity<ApiResponse<CursorResult<PostResponse>>> getOwnerPostsByStore(
        @Parameter(description = "가게 ID") @PathVariable Long storeId,
        @Parameter(description = "이전 페이지 마지막 게시글 ID, 첫 페이지는 null") @RequestParam(name = "cursorPostId", required = false) Long cursorPostId,
        @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        CursorResult<PostResponse> response = postService.getPostsByStoreAndRole(storeId, Role.OWNER,
            cursorPostId, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "가게별 일반 유저 게시글 조회", description = "특정 가게의 일반 유저 게시글을 커서 기반 페이징으로 조회합니다.")
    @GetMapping("/store/{storeId}/customers")
    public ResponseEntity<ApiResponse<CursorResult<PostResponse>>> getCustomerPostsByStore(
        @Parameter(description = "가게 ID") @PathVariable Long storeId,
        @Parameter(description = "이전 페이지 마지막 게시글 ID, 첫 페이지는 null") @RequestParam(name = "cursorPostId", required = false) Long cursorPostId,
        @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        CursorResult<PostResponse> response = postService.getPostsByStoreAndRole(storeId,
            Role.CUSTOMER, cursorPostId, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "최신 게시글 목록 조회 (추천)", description = "모든 게시글을 최신순으로 커서 기반 페이징하여 조회합니다.")
    @GetMapping("/recommendPosts")
    public ResponseEntity<ApiResponse<CursorResult<PostResponse>>> getPostsByStore(
        @Parameter(description = "이전 페이지 마지막 게시글 ID, 첫 페이지는 null") @RequestParam(name = "cursorPostId", required = false) Long cursorPostId,
        @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        // TODO : 추후 유저 기반 추천 게시글로 변경 예정
        CursorResult<PostResponse> response = postService.latestPosts(cursorPostId, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @SecurityRequirement(name = "bearer-key")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
        @PathVariable Long postId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.deletePost(postId, userDetails.getUser().getUserId());
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "댓글 작성", description = "특정 게시글에 댓글을 작성합니다.")
    @SecurityRequirement(name = "bearer-key")
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<Void>> createComment(
        @PathVariable Long postId,
        @RequestBody CommentRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.createComment(postId, request, userDetails.getUser().getUserId());
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "댓글 조회", description = "특정 게시글의 모든 댓글을 조회합니다.")
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(@PathVariable Long postId) {
        List<CommentResponse> comments = commentService.getComments(postId);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    @Operation(summary = "팔로우한 가게 게시글 조회", description = "로그인한 유저가 팔로우한 가게들의 게시글을 최신순으로 조회합니다.")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/followed")
    public ResponseEntity<ApiResponse<CursorResult<PostResponse>>> getPostsFromFollowedStores(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Parameter(description = "이전 페이지 마지막 게시글 ID, 첫 페이지는 null") @RequestParam(name = "cursorPostId", required = false) Long cursorPostId,
        @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        CursorResult<PostResponse> response = postService.getPostsFromFollowedStores(
            userDetails.getUser().getUserId(), cursorPostId, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "AI 마케팅 게시글 생성", description = "이미지와 키워드를 기반으로 AI가 마케팅 게시글을 생성합니다.")
    @SecurityRequirement(name = "bearer-key")
    @PostMapping(value = "/generate/{storeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> generateMarketingPost(
        @PathVariable Long storeId,
        @RequestPart("image") MultipartFile image,
        @RequestPart("text") String text
    ) throws IOException {
        String generatedPost = postService.generatePost(storeId, text, image);
        return ResponseEntity.ok(generatedPost);
    }

}
