package org.netway.dongnehankki.store.exception;

import org.netway.dongnehankki.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class UnregisterdStarException extends CustomException {
	private static final String MESSAGE = "존재하지 않는 평점 입니다.";

	public UnregisterdStarException() { super(MESSAGE, HttpStatus.NOT_FOUND); }
}
