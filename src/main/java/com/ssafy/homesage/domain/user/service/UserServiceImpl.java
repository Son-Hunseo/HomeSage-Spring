package com.ssafy.homesage.domain.user.service;

import com.ssafy.homesage.domain.user.exception.MismatchPasswordException;
import com.ssafy.homesage.domain.user.mapper.AuthMapper;
import com.ssafy.homesage.domain.user.mapper.UserMapper;
import com.ssafy.homesage.domain.user.model.dto.*;
import com.ssafy.homesage.domain.user.model.entity.User;
import com.ssafy.homesage.global.util.HashUtil;
import com.ssafy.homesage.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final AuthMapper authMapper;
    private final HashUtil hashUtil;
    private final JwtUtil jwtUtil;

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
    @Transactional
    public void changedPassword(String accessToken, UserChangedPwRequestDto userChangedPwRequestDto) {
        log.info("[UserService changedPassword()] accessToken: {}", accessToken);
        // 토큰에서 사용자의 이메일 추출
        String userEmail = jwtUtil.getUserEmail(accessToken, "AccessToken");

        // 사용자의 이메일을 통해 비밀번호 조회
        String findPassword = userMapper.findPasswordByEmail(userEmail);
        log.info("[UserService changedPassword()] findPassword: {}", findPassword);

        // 조회한 비밀번호와 입력 된 비밀번호가 일치하지 않으면 예외 처리
        String hashedPassword = hashUtil.getDigest(userChangedPwRequestDto.password());
        if(!findPassword.equals(hashedPassword)) {
            throw new MismatchPasswordException();
        }

        // 일치한다면 비밀번호 업데이트
        userMapper.updateNewPassword(userEmail, hashUtil.getDigest(userChangedPwRequestDto.newPassword()));

        // 로그아웃 처리
        authMapper.updateValidTokenToInvalidByUserEmail(userEmail);
    }
}
