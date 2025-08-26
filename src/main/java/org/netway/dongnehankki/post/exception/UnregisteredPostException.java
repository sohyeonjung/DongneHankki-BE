package org.netway.dongnehankki.post.exception;

import org.netway.dongnehankki.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class UnregisteredPostException extends CustomException {
    private static final String MESSAGE = "존재하지 않는 게시글 입니다.";

    public UnregisteredPostException() {
        super(MESSAGE, HttpStatus.NOT_FOUND);
    }
}
