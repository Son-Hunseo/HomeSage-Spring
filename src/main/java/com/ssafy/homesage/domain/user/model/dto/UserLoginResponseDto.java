package com.ssafy.homesage.domain.user.model.dto;

import lombok.Builder;

@Builder
public record UserLoginResponseDto(String name, String accessToken, String refreshToken, Long maxAge) {
}
