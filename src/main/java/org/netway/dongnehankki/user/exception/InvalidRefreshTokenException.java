package org.netway.dongnehankki.user.exception;

import org.netway.dongnehankki.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends CustomException {
    private static final String MESSAGE = "유효하지 않은 리프레시 토큰입니다.";

    public InvalidRefreshTokenException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }
}