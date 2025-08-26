package org.netway.dongnehankki.post.exception;

import org.netway.dongnehankki.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NotLikedException extends CustomException {
    private static final String MESSAGE = "좋아요를 누르지 않았던 게시글 입니다.";

    public NotLikedException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
