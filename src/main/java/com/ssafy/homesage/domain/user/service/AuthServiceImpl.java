package com.ssafy.homesage.domain.user.service;

import com.ssafy.homesage.domain.user.exception.DuplicatedEmailException;
import com.ssafy.homesage.domain.user.exception.MismatchPasswordException;
import com.ssafy.homesage.domain.user.exception.UserNotFoundException;
import com.ssafy.homesage.domain.user.mapper.AuthMapper;
import com.ssafy.homesage.domain.user.model.dto.UserLoginRequestDto;
import com.ssafy.homesage.domain.user.model.dto.UserLoginResponseDto;
import com.ssafy.homesage.domain.user.model.dto.UserSignUpRequestDto;
import com.ssafy.homesage.domain.user.model.entity.User;
import com.ssafy.homesage.domain.user.model.jwt.Token;
import com.ssafy.homesage.global.util.FormatUtil;
import com.ssafy.homesage.global.util.HashUtil;
import com.ssafy.homesage.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthMapper authMapper;
    private final HashUtil hashUtil;
    private final JwtUtil jwtUtil;

    @Override
    public void signUp(UserSignUpRequestDto userSignUpRequestDto) {
        log.info("[AuthService signUp()] signUp start");
        // 비밀번호와 비밀번호 확인이 다른 경우 예외 발생.
        if(!userSignUpRequestDto.password().equals(userSignUpRequestDto.retryPassword())) {
            throw new MismatchPasswordException();
        }

        // 비밀번호를 해시 후 전달.
        UserSignUpRequestDto signUpRequestDto = UserSignUpRequestDto.builder()
                .email(userSignUpRequestDto.email())
                .password(hashUtil.getDigest(userSignUpRequestDto.password()))
                .name(userSignUpRequestDto.name())
                .role(userSignUpRequestDto.role())
                .build();

        authMapper.saveUser(signUpRequestDto);
    }

    @Override
    public void checkEmail(String email) {
        // 이메일 양식이 아니거나, 이미 존재하는 이메일인 경우 예외 발생.
        if(!FormatUtil.isValidEmail(email) || authMapper.selectCountByEmail(email) != 0) {
            throw new DuplicatedEmailException();
        }
    }

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto userLoginRequestDto) {
        // 비밀번호를 암호화 한 Dto 새로 생성
        UserLoginRequestDto loginRequestDto = UserLoginRequestDto.builder()
                .email(userLoginRequestDto.email())
                .password(hashUtil.getDigest(userLoginRequestDto.password()))
                .build();

        // request dto 와 일치하는 user 조회 및 에러 처리
        User findUser = authMapper.findByEmailAndPassword(loginRequestDto)
                .orElseThrow(() -> new UserNotFoundException());

        // Access Token 및 Refresh Token 발급
        Token accessToken = jwtUtil.generateToken(findUser, "AccessToken");
        Token refreshToken = jwtUtil.generateToken(findUser, "RefreshToken");

        // 발급한 토큰을 token_status 테이블에 저장
        authMapper.saveToken(accessToken.userEmail(), accessToken.hashedToken(), accessToken.getDateExpiration());
        authMapper.saveToken(refreshToken.userEmail(), refreshToken.hashedToken(), refreshToken.getDateExpiration());

        // Response Dto 설정
        return UserLoginResponseDto.builder()
                .accessToken(accessToken.token())
                .refreshToken(refreshToken.token())
                .maxAge(refreshToken.expiration())
                .build();
    }

    @Override
    public int logout(String accessToken) {
        // 토큰에서 사용자의 이메일 추출
        String userEmail = jwtUtil.getUserEmail(accessToken, "AccessToken");
        log.info("[AuthService logout()] userEmail: {}", userEmail);

        // 사용자의 활성화 된 토큰을 모두 비활성화 처리
        return authMapper.updateValidTokenToInvalidByUserEmail(userEmail);
    }

    @Override
    public Token reGenerateToken(String refreshToken) {
        // refreshToken 에서 사용자 이메일 추출
        String userEmail = jwtUtil.getUserEmail(refreshToken, "RefreshToken");

        // refreshToken 을 제외한 기존 발급 토큰 모두 비활성화 처리
        authMapper.updateValidTokenToInvalidByUserEmailAndRefreshToken(
                userEmail, hashUtil.getDigest(refreshToken));

        // 해당 사용자가 존재하는지 재확인 후, 새로운 AccessToken 발급
        User findUser = authMapper.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException());
        Token newAccessToken = jwtUtil.generateToken(findUser, "AccessToken");

        // 새로 발급 받은 토큰을 저장
        authMapper.saveToken(newAccessToken.userEmail(), newAccessToken.hashedToken(), newAccessToken.getDateExpiration());

        return newAccessToken;
    }

    @Override
    public boolean isValidToken(String token) {
        token = hashUtil.getDigest(token);
        return authMapper.findValidStatusByToken(token);
    }
}
