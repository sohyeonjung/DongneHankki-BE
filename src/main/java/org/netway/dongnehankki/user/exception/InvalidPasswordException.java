package org.netway.dongnehankki.user.exception;

import org.netway.dongnehankki.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends CustomException {
    private static final String MESSAGE = "비밀번호가 일치하지 않습니다.";

    public InvalidPasswordException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }
}