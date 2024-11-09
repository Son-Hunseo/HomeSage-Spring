package com.ssafy.homesage.domain.user.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    // 더미데이터 업데이트 이후에 ADMIN 지우기
    ADMIN("ADMIN"), CONSUMER("CONSUMER"), PROVIDER("PROVIDER");

    private final String role;

}
