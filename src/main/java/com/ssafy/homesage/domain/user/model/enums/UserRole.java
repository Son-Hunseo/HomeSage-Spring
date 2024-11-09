package com.ssafy.homesage.domain.user.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    CONSUMER("CONSUMER"), PROVIDER("PROVIDER");

    private final String role;

}
