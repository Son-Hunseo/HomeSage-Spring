package com.ssafy.homesage.domain.user.service;

import com.ssafy.homesage.domain.user.model.dto.UserLoginRequestDto;
import com.ssafy.homesage.domain.user.model.dto.UserLoginResponseDto;
import com.ssafy.homesage.domain.user.model.dto.UserSignUpRequestDto;
import com.ssafy.homesage.domain.user.model.jwt.Token;

public interface AuthService {
    void signUp(UserSignUpRequestDto userSignUpRequestDto);

    UserLoginResponseDto login(UserLoginRequestDto userLoginRequestDto);

    int logout(String accessToken);

    Token reGenerateToken(String refreshToken);

    boolean isValidToken(String token);

    void checkEmail(String email);
}
