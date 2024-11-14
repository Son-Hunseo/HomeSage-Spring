package com.ssafy.homesage.global.error;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse {

	private int httpStatus;
	private final String message;
	private final StringBuilder sb = new StringBuilder();

	
	public ErrorResponse(ErrorCode errorCode) {
		this.httpStatus = errorCode.getHttpStatus();
		this.message = errorCode.getMessage();
	}
	
	public static ErrorResponse of(ErrorCode errorCode) {
		return new ErrorResponse(errorCode);
	}
	
	/**
	 * JwtFilter에서 응답하기위한 포맷 설정.
	 */
	public String toString() {
		sb.append("{").append("\n")
			.append("\"httpStatus\": ").append(this.httpStatus).append("\n")
			.append("\"message\": ").append("\"").append(this.message).append("\"").append("\n")
			.append("}");
		
		return sb.toString();
	}
}
