package org.netway.dongnehankki.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

	BAD_REQUEST("400", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
	INTERNAL_SERVER_ERROR("500", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	EXTERNAL_SERVICE_ERROR("500", "외부 서비스 연동 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

	private final HttpStatus status;
	private final String code;
	private String message;

	ErrorCode(final String code, final String message, final HttpStatus status) {
		this.status = status;
		this.code = code;
		this.message = message;
	}
}
