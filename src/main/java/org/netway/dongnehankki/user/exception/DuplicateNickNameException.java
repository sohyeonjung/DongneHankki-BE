package org.netway.dongnehankki.user.exception;

import org.netway.dongnehankki.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class DuplicateNickNameException extends CustomException {
    private static final String MESSAGE = "이미 사용 중인 닉네임입니다.";

    public DuplicateNickNameException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
