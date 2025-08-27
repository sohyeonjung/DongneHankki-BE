package org.netway.dongnehankki.follow.exception;

import org.netway.dongnehankki.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class AlreadyFollowedException extends CustomException {
    private static final String MESSAGE = "이미 팔로우한 가게입니다.";

    public AlreadyFollowedException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
