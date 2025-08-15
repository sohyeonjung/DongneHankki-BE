package org.netway.dongnehankki.store.exception;

import org.netway.dongnehankki.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class ReviewStoreMismatchException extends CustomException {
	private static final String MESSAGE = "주어진 입력 Store와 Reivew에 해당하는 Store가 다른 값입니다.";

	public ReviewStoreMismatchException() { super(MESSAGE, HttpStatus.BAD_REQUEST);}
}
