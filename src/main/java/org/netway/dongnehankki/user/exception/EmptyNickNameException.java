package org.netway.dongnehankki.user.exception;

import org.netway.dongnehankki.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class EmptyNickNameException extends CustomException {
    private static final String MESSAGE = "닉네임은 비어있을 수 없습니다.";

    public EmptyNickNameException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
