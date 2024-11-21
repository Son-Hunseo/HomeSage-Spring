package com.ssafy.homesage.domain.user.model.dto;

import lombok.Builder;

@Builder
public record UserChangedPwRequestDto(String currentPassword, String newPassword, String confirmPassword) {
}
