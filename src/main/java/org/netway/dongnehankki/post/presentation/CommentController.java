package org.netway.dongnehankki.post.presentation;

import lombok.RequiredArgsConstructor;
import org.netway.dongnehankki.global.auth.CustomUserDetails;
import org.netway.dongnehankki.global.common.ApiResponse;
import org.netway.dongnehankki.post.application.CommentService;
import org.netway.dongnehankki.post.dto.request.CommentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.updateComment(commentId, request, userDetails.getUser().getUserId());
        return ResponseEntity.ok(ApiResponse.success());
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails.getUser().getUserId());
        return ResponseEntity.ok(ApiResponse.success());
    }
}
