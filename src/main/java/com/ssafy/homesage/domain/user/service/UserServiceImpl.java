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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    @Transactional
    public Map<String, Boolean> interest(String accessToken, Long saleId) {
        log.info("[UserService interest()] accessToken: {}", accessToken);
        // 토큰에서 사용자의 이메일 추출
        String userEmail = jwtUtil.getUserEmail(accessToken, "AccessToken");

        // 이메일을 통해 userId 조회
        Long userId = userMapper.findIdByEmail(userEmail);

        // saleId 와 사용자 이메일을 통해 찜목록에 데이터가 있는지 확인
        int count = userMapper.findInterestBySaleIdAndUserId(saleId, userId);

        Map<String, Boolean> resultMap = new HashMap<>();
        if (count == 0) {
            // 없다면, 찜목록에 추가 후 map 에 true 를 담는다.
            userMapper.insertInterest(saleId, userId);
            resultMap.put("isInterest", true);
        } else {
            // 있다면, 찜목록에서 삭제 후 map 에 false 를 담는다.
            userMapper.deleteInterest(saleId, userId);
            resultMap.put("isInterest", false);
        }
        return resultMap;
    }
}
