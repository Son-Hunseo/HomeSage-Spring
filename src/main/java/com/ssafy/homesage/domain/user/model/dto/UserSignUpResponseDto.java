package com.ssafy.homesage.domain.user.model.dto;

// ex) 홍길동님 회원가입이 정상적으로 처리되었습니다. 이런 메시지를 위한 DTO
public record UserSignUpResponseDto(String name) {
}
