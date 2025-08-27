package org.netway.dongnehankki.post.exception;

import org.netway.dongnehankki.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class UnregisteredCommentException extends CustomException {
    private static final String MESSAGE = "존재하지 않는 댓글 입니다.";

    public UnregisteredCommentException() {
        super(MESSAGE, HttpStatus.NOT_FOUND);
    }
}
