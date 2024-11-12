package com.ssafy.homesage.domain.user.model.entity;

import com.ssafy.homesage.domain.user.model.enums.UserRole;
import com.ssafy.homesage.global.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    private String email;
    private String password;
    private String name;
    private UserRole role;

}