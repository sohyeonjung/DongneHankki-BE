package org.netway.dongnehankki.post.presentation;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.netway.dongnehankki.global.auth.CustomUserDetails;
import org.netway.dongnehankki.global.common.ApiResponse;
import org.netway.dongnehankki.post.application.PostService;
import org.netway.dongnehankki.post.dto.request.PostCreateRequest;
import org.netway.dongnehankki.post.dto.response.CursorResult;
import org.netway.dongnehankki.post.dto.response.PostResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<Void>> createPost(
        @ModelAttribute PostCreateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.createPost(request, userDetails.getUser().getUserId());
        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(@PathVariable Long postId) {
        PostResponse response = postService.getPost(postId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<ApiResponse<CursorResult<PostResponse>>> getPostsByStore(
            @PathVariable Long storeId,
            @RequestParam(name = "cursorPostId", required = false) Long cursorPostId,
            @RequestParam(defaultValue = "10") int size) {
        CursorResult<PostResponse> response = postService.getPostsByStore(storeId, cursorPostId, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

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
