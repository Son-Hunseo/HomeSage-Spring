package com.ssafy.homesage.domain.user.controller;

import java.util.List;

import com.ssafy.homesage.domain.user.exception.DuplicatedEmailException;
import com.ssafy.homesage.domain.user.exception.LoginFailException;
import com.ssafy.homesage.domain.user.model.dto.*;
import com.ssafy.homesage.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // 서버 테스트용
    // 모든 유저 반환
    @GetMapping
    private ResponseEntity<?> getFirstUser() {
        List<UserTestResponseDto> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    // 회원가입
    @PostMapping
    private ResponseEntity<?> signUp(@RequestBody UserSignUpRequestDto userSignUpRequestDto) {
        try {
            UserSignUpResponseDto userSignUpResponseDto = userService.signUp(userSignUpRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(userSignUpResponseDto);
        } catch (DuplicatedEmailException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }
    }

    // 로그인
    @PostMapping("/login")
    private ResponseEntity<?> login(@RequestBody UserLoginRequestDto userLoginRequestDto) {
        try {
            UserLoginResponseDto userLoginResponseDto = userService.login(userLoginRequestDto);
            return ResponseEntity.status(HttpStatus.OK).body(userLoginResponseDto);
        } catch (LoginFailException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login Fail");
        }
    }

}
