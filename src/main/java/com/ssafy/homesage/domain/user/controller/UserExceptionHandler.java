package com.ssafy.homesage.domain.user.controller;

import com.ssafy.homesage.domain.user.exception.DuplicatedEmailException;
import com.ssafy.homesage.domain.user.exception.MismatchPasswordException;
import com.ssafy.homesage.domain.user.exception.UserNotFoundException;
import com.ssafy.homesage.global.error.ErrorCode;
import com.ssafy.homesage.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.InputMismatchException;

@RestControllerAdvice(basePackages = {"com.ssafy.homesage.domain.user"})
public class UserExceptionHandler {

//    @Operation(summary = "공통 서버 오류")
//    @ExceptionHandler(value = {RuntimeException.class})
//    public ResponseEntity<?>

    @Operation(summary = "회원가입 시 이메일 중복")
    @ExceptionHandler(value = {DuplicatedEmailException.class})
    public ResponseEntity<?> duplicatedEmailHandler(RuntimeException e) {

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.DUPLICATED_EMAIL);

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @Operation(summary = "회원가입 시 입력한 두 비밀번호가 서로 다름")
    @ExceptionHandler(value = {MismatchPasswordException.class})
    public ResponseEntity<?> mismatchPasswordHandler(InputMismatchException e) {

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.BAD_REQUEST);

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @Operation(summary = "토큰 재발급 시 유저 정보를 찾을 수 없음")
    @ExceptionHandler(value = {UserNotFoundException.class})
    public ResponseEntity<?> userNotFoundHandler(NullPointerException e) {

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.USER_NOT_FOUND);

        return ResponseEntity.badRequest().body(errorResponse);
    }
}
