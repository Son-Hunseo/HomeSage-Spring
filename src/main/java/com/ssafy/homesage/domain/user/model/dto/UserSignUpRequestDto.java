package com.ssafy.homesage.domain.user.model.dto;

import com.ssafy.homesage.domain.user.model.enums.UserRole;

public record UserSignUpRequestDto(String email, String password, String name, UserRole role) {
}
