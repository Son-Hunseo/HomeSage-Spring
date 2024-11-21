package com.ssafy.homesage.domain.user.controller;

import com.ssafy.homesage.domain.sale.exception.EmptySalesException;
import com.ssafy.homesage.domain.user.exception.*;
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

    @Operation(summary = "RefreshToken 정보가 없음")
    @ExceptionHandler(value = {EmptyTokenException.class})
    public ResponseEntity<?> emptyTokenHandler(NullPointerException e) {

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.USER_NOT_FOUND);

        return ResponseEntity.badRequest().body(errorResponse);
    }

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

    @Operation(summary = "찜목록이 없음")
    @ExceptionHandler(value = {EmptyInterestedSalesException.class})
    public ResponseEntity<?> emptyInterestedSalesHandler(NullPointerException e) {

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.EMPTY_INTERESTED);

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @Operation(summary = "이미 예약 된 건물")
    @ExceptionHandler(value = {DuplicateReservationException.class})
    public ResponseEntity<?> duplicateReservationHandler(RuntimeException e) {

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.DUPLICATE_RESERVATION);

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @Operation(summary = "예약목록이 없음")
    @ExceptionHandler(value = {EmptyReservesException.class})
    public ResponseEntity<?> emptyReservesHandler(NullPointerException e) {

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.EMPTY_RESERVES);

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @Operation(summary = "관리 중인 상품이 없음")
    @ExceptionHandler(value = {EmptyManageSalesException.class})
    public ResponseEntity<?> emptyManageSalesHandler(NullPointerException e) {

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.EMPTY_MANAGE_SALES);

        return ResponseEntity.badRequest().body(errorResponse);
    }
}
