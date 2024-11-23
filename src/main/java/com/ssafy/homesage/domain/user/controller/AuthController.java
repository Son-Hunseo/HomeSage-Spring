package com.ssafy.homesage.domain.user.controller;

import com.ssafy.homesage.domain.user.model.dto.UserLoginRequestDto;
import com.ssafy.homesage.domain.user.model.dto.UserLoginResponseDto;
import com.ssafy.homesage.domain.user.model.dto.UserSignUpRequestDto;
import com.ssafy.homesage.domain.user.model.jwt.Token;
import com.ssafy.homesage.domain.user.service.AuthService;
import com.ssafy.homesage.global.util.HeaderUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "인증 및 인가 컨트롤러", description = "회원가입, 로그인, 로그아웃, 비밀번호 변경, 회원탈퇴")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${server.ip}")
    private String serverIp;

    @Operation(summary = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "회원가입 실패")
    })
    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody UserSignUpRequestDto userSignUpRequestDto) {
        authService.signUp(userSignUpRequestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @Operation(summary = "회원가입 시 중복 이메일 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "중복 이메일 없음"),
            @ApiResponse(responseCode = "409", description = "중복 된 이메일")
    })
    @GetMapping("/checkEmail/{email}")
    public ResponseEntity<?> checkEmail(@PathVariable String email) {

        // 클라이언트로부터 전달받은 이메일을 검증.
        authService.checkEmail(email);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequestDto userLoginRequestDto) {
        // AccessToken, RefreshToken 생성
        UserLoginResponseDto userLoginResponseDto = authService.login(userLoginRequestDto);

        // AccessToken 을 Http Header 의 Authorization 으로 전달
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HeaderUtil.getAuthorizationHeaderName(),
                HeaderUtil.getTokenPrefix() + userLoginResponseDto.accessToken());

        // RefreshToken 을 HttpOnly Cookie 로 전달
        ResponseCookie responseCookie = ResponseCookie
                .from(HeaderUtil.getRefreshCookieName(), userLoginResponseDto.refreshToken())
                .domain(serverIp) // 어떤 사이트에서 쿠키를 사용할 수 있도록 허용할지 설정
                .path("/") // 사이트 내 쿠키 허용 경로 설정
                .httpOnly(true) // HTTP 통신을 위해서만 사용하도록 설정
                .secure(false) // Set-Cookie 설정
                .maxAge(userLoginResponseDto.maxAge() / 1000) // RefreshToken 과 동일한 만료 시간으로 설정
                .sameSite("None") // 동일한 사이트에서 사용할 수 있도록 설정 : None : 동일한 사이트가 아니어도 된다.
                .build();

        return ResponseEntity.ok()
                .headers(httpHeaders).header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .build();
    }

    @Operation(summary = "로그아웃")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "500", description = "로그아웃 실패")
    })
    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // Http Header 의 Authorization (Access Token) 추출
        String accessToken = HeaderUtil.getAccessToken(request);

        // 로그아웃
        authService.logout(accessToken);

        // RefreshToken 삭제
        HttpHeaders httpHeaders = new HttpHeaders();
        ResponseCookie responseCookie = ResponseCookie
                .from(HeaderUtil.getRefreshCookieName(), "")
                .domain(serverIp)
                .path("/")
                .httpOnly(true)
                .secure(false)
                .maxAge(0)
                .sameSite("None")
                .build();

        return ResponseEntity.noContent()
                .headers(httpHeaders).header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .build();
    }

    @Operation(summary = "Access Token 재발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "토큰 발급 성공"),
            @ApiResponse(responseCode = "500", description = "토큰 발급 실패")
    })
    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        // RefreshToken 추출
        // Client 에서 withCredentials 옵션이 설정되어 있어야 한다.
        String refreshToken = HeaderUtil.getRefreshToken(request);

        // RefreshToken 으로 새로운 AccessToken 발급
        Token newAccessToken = authService.reGenerateToken(refreshToken);

        // 새로운 AccessToken 을 Header 에 추가
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HeaderUtil.getAuthorizationHeaderName(),
                HeaderUtil.getTokenPrefix() + newAccessToken.token());

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .build();
    }
}
