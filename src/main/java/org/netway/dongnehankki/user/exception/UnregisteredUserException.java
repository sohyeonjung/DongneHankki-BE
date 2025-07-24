package org.netway.dongnehankki.user.exception;

import org.netway.dongnehankki.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class UnregisteredUserException extends CustomException {
    private static final String MESSAGE = "가입되지 않은 id,pw 입니다";

    public UnregisteredUserException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }
}