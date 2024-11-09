package com.ssafy.homesage.domain.user.model.dto;

import com.ssafy.homesage.domain.user.model.entity.User;

public record UserTestResponseDto(String email, String name) {
    public UserTestResponseDto(User user) {
        this(user.getEmail(), user.getName());
    }
}