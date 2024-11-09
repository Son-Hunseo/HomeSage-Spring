package com.ssafy.homesage.domain.user.service;

import com.ssafy.homesage.domain.user.exception.DuplicatedEmailException;
import com.ssafy.homesage.domain.user.mapper.UserMapper;
import com.ssafy.homesage.domain.user.model.dto.UserGetResponseDto;
import com.ssafy.homesage.domain.user.model.dto.UserSignUpRequestDto;
import com.ssafy.homesage.domain.user.model.dto.UserSignUpResponseDto;
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
    public List<UserGetResponseDto> getAllUsers() {

        List<User> allUsers = userMapper.findAllUsers();
        List<UserGetResponseDto> allUserInfo = new ArrayList<>();

        for (User user : allUsers) {
            allUserInfo.add(new UserGetResponseDto(user));
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
}
