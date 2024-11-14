package com.ssafy.homesage.domain.user.service;

import com.ssafy.homesage.domain.user.mapper.UserMapper;
import com.ssafy.homesage.domain.user.model.dto.*;
import com.ssafy.homesage.domain.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public List<UserTestResponseDto> getAllUsers() {

        List<User> allUsers = userMapper.findAllUsers();
        List<UserTestResponseDto> allUserInfo = new ArrayList<>();

        for (User user : allUsers) {
            allUserInfo.add(new UserTestResponseDto(user));
        }

        return allUserInfo;
    }
}
