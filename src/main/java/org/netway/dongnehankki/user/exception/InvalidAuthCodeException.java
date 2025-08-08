package org.netway.dongnehankki.user.exception;

import org.netway.dongnehankki.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidAuthCodeException extends CustomException {
    private static final String MESSAGE = "유효하지 않은 인증 코드입니다.";

    public InvalidAuthCodeException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }
}