package org.netway.dongnehankki.store.exception;

import org.netway.dongnehankki.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class UnregisteredReviewException extends CustomException {
	private static final String MESSAGE = "존재하지 않는 리뷰 입니다.";
	
	public UnregisteredReviewException() { super(MESSAGE, HttpStatus.NOT_FOUND); }
}
