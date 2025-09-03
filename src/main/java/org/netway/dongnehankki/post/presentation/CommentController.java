package org.netway.dongnehankki.post.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.netway.dongnehankki.global.auth.CustomUserDetails;
import org.netway.dongnehankki.global.common.ApiResponse;
import org.netway.dongnehankki.post.application.CommentService;
import org.netway.dongnehankki.post.dto.request.CommentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "댓글", description = "댓글 수정/삭제 API")
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 수정", description = "사용자가 작성한 댓글을 수정합니다.")
    @PatchMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @Parameter(description = "수정할 댓글 ID") @PathVariable Long commentId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.updateComment(commentId, request, userDetails.getUser().getUserId());
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "댓글 삭제", description = "사용자가 작성한 댓글을 삭제합니다.")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @Parameter(description = "삭제할 댓글 ID") @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails.getUser().getUserId());
        return ResponseEntity.ok(ApiResponse.success());
    }
}
