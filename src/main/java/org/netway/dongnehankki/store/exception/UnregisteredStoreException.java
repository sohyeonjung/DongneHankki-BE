package org.netway.dongnehankki.store.exception;

import org.netway.dongnehankki.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class UnregisteredStoreException extends CustomException {
    private static final String MESSAGE = "존재하지 않는 가게 입니다.";

    public UnregisteredStoreException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }
}