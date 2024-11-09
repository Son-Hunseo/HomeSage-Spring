package com.ssafy.homesage.domain.user.model.entity;


import com.ssafy.homesage.domain.user.model.enums.UserRole;
import com.ssafy.homesage.global.entity.BaseEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class User extends BaseEntity {

    private String email;
    private String password;
    private String name;
    private UserRole role;

    @Builder
    public User(String email, String password, String name, UserRole role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }
}