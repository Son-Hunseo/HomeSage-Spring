package com.ssafy.homesage.domain.user.controller;

import java.util.List;

import com.ssafy.homesage.domain.user.exception.DuplicatedEmailException;
import com.ssafy.homesage.domain.user.model.dto.UserTestResponseDto;
import com.ssafy.homesage.domain.user.model.dto.UserSignUpRequestDto;
import com.ssafy.homesage.domain.user.model.dto.UserSignUpResponseDto;
import com.ssafy.homesage.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
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

}
