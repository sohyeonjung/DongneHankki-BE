package org.netway.dongnehankki.store.exception;

import org.netway.dongnehankki.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class UnregisteredMenuException extends CustomException {
	private static final String MESSAGE = "존재하지 않는 메뉴 입니다.";

	public UnregisteredMenuException() {
		super(MESSAGE, HttpStatus.NOT_FOUND);
	}
}
