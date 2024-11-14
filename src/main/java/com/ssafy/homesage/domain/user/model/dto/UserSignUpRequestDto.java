package com.ssafy.homesage.domain.user.model.dto;

import com.ssafy.homesage.domain.user.model.enums.UserRole;
import lombok.Builder;

@Builder
public record UserSignUpRequestDto(String email, String password, String retryPassword, String name, UserRole role) {
}
