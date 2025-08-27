package org.netway.dongnehankki.post.exception;

import org.netway.dongnehankki.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class AlreadyLikedException extends CustomException {
    private static final String MESSAGE = "이미 좋아요를 누른 게시글 입니다.";

    public AlreadyLikedException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
