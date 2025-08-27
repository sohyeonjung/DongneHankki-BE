package org.netway.dongnehankki.follow.exception;

import org.netway.dongnehankki.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NotFollowedException extends CustomException {
    private static final String MESSAGE = "팔로우 하지 않았던 가게입니다.";

    public NotFollowedException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
