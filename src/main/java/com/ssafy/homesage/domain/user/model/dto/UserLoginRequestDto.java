package com.ssafy.homesage.domain.user.model.dto;

import lombok.Builder;

@Builder
public record UserLoginRequestDto(String email, String password) {
}
