package org.netway.dongnehankki.post.exception;

import org.netway.dongnehankki.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class UserNotMatchedException extends CustomException {
    private static final String MESSAGE = "작성자와 일치하지 않습니다.";

    public UserNotMatchedException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}