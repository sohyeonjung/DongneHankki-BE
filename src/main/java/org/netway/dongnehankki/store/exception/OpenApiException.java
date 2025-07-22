package org.netway.dongnehankki.store.exception;

import org.netway.dongnehankki.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class OpenApiException extends CustomException {
	private static final String MESSAGE = "외부 API 연동 중 오류가 발생하였습니다.";

	public OpenApiException() { super(MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR); }
}
