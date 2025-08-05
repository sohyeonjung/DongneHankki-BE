package org.netway.dongnehankki.post.presentation;

import lombok.RequiredArgsConstructor;
import org.netway.dongnehankki.global.auth.CustomUserDetails;
import org.netway.dongnehankki.global.common.ApiResponse;
import org.netway.dongnehankki.post.application.PostService;
import org.netway.dongnehankki.post.dto.request.PostCreateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createPost(
        @RequestBody PostCreateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.createPost(request, userDetails.getUser().getUserId());
        return ResponseEntity.ok(ApiResponse.success());
    }

}
