package org.netway.dongnehankki.post.application;

import java.util.List;
import org.netway.dongnehankki.post.dto.request.CommentRequest;
import org.netway.dongnehankki.post.dto.response.CommentResponse;

public interface CommentService {
    void createComment(Long postId, CommentRequest request, Long userId);
    List<CommentResponse> getComments(Long postId);
    void updateComment(Long commentId, CommentRequest request, Long userId);
    void deleteComment(Long commentId, Long userId);
}
