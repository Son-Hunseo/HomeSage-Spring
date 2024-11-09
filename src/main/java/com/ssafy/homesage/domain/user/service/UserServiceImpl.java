package com.ssafy.homesage.domain.user.service;

import com.ssafy.homesage.domain.user.exception.DuplicatedEmailException;
import com.ssafy.homesage.domain.user.exception.LoginFailException;
import com.ssafy.homesage.domain.user.mapper.UserMapper;
import com.ssafy.homesage.domain.user.model.dto.*;
import com.ssafy.homesage.domain.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserTestResponseDto> getAllUsers() {

        List<User> allUsers = userMapper.findAllUsers();
        List<UserTestResponseDto> allUserInfo = new ArrayList<>();

        for (User user : allUsers) {
            allUserInfo.add(new UserTestResponseDto(user));
        }

        return allUserInfo;
    }

    @Override
    public UserSignUpResponseDto signUp(UserSignUpRequestDto userSignUpRequestDto)
            throws DuplicatedEmailException {

        // 아이디 중복검사
        if (userMapper.checkDuplicateEmail(userSignUpRequestDto.email())) {
            throw new DuplicatedEmailException();
        }
        else {
            User user = User.builder()
                    .email(userSignUpRequestDto.email())
                    .name(userSignUpRequestDto.name())
                    .password(passwordEncoder.encode(userSignUpRequestDto.password())) // 암호화
                    .role(userSignUpRequestDto.role())
                    .build();

            userMapper.signUp(user);

            return new UserSignUpResponseDto(user.getName());
        }
    }

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto userLoginRequestDto) throws LoginFailException {

        User user = userMapper.login(userLoginRequestDto.email());
        String enteredPassword = userLoginRequestDto.password();

        if (passwordEncoder.matches(enteredPassword, user.getPassword())) {
            return new UserLoginResponseDto(user.getName());
        }
        else {
            throw new LoginFailException();
        }

    }
}
