package com.ssafy.homesage.domain.user.mapper;

import java.util.List;

import com.ssafy.homesage.domain.user.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    List<User> findAllUsers();

    boolean checkDuplicateEmail(String email);

    void signUp(User user);
}