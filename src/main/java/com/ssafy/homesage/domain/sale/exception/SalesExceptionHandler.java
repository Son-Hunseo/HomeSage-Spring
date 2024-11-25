package com.ssafy.homesage.domain.sale.exception;

import com.ssafy.homesage.domain.user.exception.*;
import com.ssafy.homesage.global.error.ErrorCode;
import com.ssafy.homesage.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.InputMismatchException;

@RestControllerAdvice(basePackages = {"com.ssafy.homesage.domain.sale"})
public class SalesExceptionHandler {

    @Operation(summary = "검색 된 상품이 없음")
    @ExceptionHandler(value = {EmptySalesException.class})
    public ResponseEntity<?> emptySalesHandler(NullPointerException e) {

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.EMPTY_SALES);

        return ResponseEntity.internalServerError().body(errorResponse);
    }
}
