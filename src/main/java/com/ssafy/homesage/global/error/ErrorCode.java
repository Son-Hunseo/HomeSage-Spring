package com.ssafy.homesage.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

	EMPTY_SALES(HttpStatus.NO_CONTENT.value(), "관리 중인 상품이 없습니다."),
	EMPTY_RESERVES(HttpStatus.NO_CONTENT.value(), "예약 목록이 없습니다."),
	DUPLICATE_RESERVATION(HttpStatus.CONFLICT.value(), "이미 예약된 건물입니다."),
	EMPTY_INTERESTED(HttpStatus.NO_CONTENT.value(), "찜목록이 비어있습니다."),
	BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다."), // 기타 잘못된 요청 처리.
	DUPLICATED_EMAIL(HttpStatus.CONFLICT.value(), "사용할 수 없는 이메일입니다."),
	REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED.value(), "비정상 토큰입니다."),
	USER_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "사용자를 찾을 수 없습니다.");
	
	private final int httpStatus;
	private final String message;
	
	ErrorCode(int httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
