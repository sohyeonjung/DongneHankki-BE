package org.netway.dongnehankki.post.application;

import lombok.RequiredArgsConstructor;
import org.netway.dongnehankki.post.domain.Comment;
import org.netway.dongnehankki.post.domain.Post;
import org.netway.dongnehankki.post.dto.request.CommentRequest;
import org.netway.dongnehankki.post.exception.UnregisteredCommentException;
import org.netway.dongnehankki.post.exception.UnregisteredPostException;
import org.netway.dongnehankki.post.exception.UserNotMatchedException;
import org.netway.dongnehankki.post.repository.CommentRepository;
import org.netway.dongnehankki.post.repository.PostRepository;
import org.netway.dongnehankki.user.domain.User;
import org.netway.dongnehankki.user.exception.UnregisteredUserException;
import org.netway.dongnehankki.user.infrastructure.UserRepository;
import org.netway.dongnehankki.post.dto.response.CommentResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createComment(Long postId, CommentRequest request, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(UnregisteredPostException::new);
        User user = userRepository.findById(userId)
                .orElseThrow(UnregisteredUserException::new);

        Comment comment = Comment.createComment(request.getContent(), post, user);

        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long postId) {
        return commentRepository.findByPost_PostId(postId).stream()
                .map(CommentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateComment(Long commentId, CommentRequest request, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(UnregisteredCommentException::new);
        if (!comment.getUser().getUserId().equals(userId)) {
            throw new UserNotMatchedException();
        }
        comment.updateComment(request.getContent());
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(UnregisteredCommentException::new);
        if (!comment.getUser().getUserId().equals(userId)) {
            throw new UserNotMatchedException();
        }
        comment.markAsDeleted();
    }
}
