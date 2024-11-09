package com.ssafy.homesage.domain.user.model.dto;

import com.ssafy.homesage.domain.user.model.entity.User;

public record UserGetResponseDto(String email, String name) {
    public UserGetResponseDto(User user) {
        this(user.getEmail(), user.getName());
    }
}