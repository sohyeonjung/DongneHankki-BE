package org.netway.dongnehankki.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

	private final HttpStatus status;

	public CustomException(final String message, final HttpStatus status) {
		super(message);
		this.status = status;
	}
}