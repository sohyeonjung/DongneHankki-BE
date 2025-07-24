package org.netway.dongnehankki.user.exception;

import org.netway.dongnehankki.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class DuplicateLoginIdException extends CustomException {
    private static final String MESSAGE = "이미 사용 중인 ID 입니다.";

    public DuplicateLoginIdException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
