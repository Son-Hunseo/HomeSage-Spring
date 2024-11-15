package com.ssafy.homesage.domain.user.controller;

import java.util.List;
import java.util.Map;

import com.ssafy.homesage.domain.user.model.dto.*;
import com.ssafy.homesage.domain.user.service.UserService;
import com.ssafy.homesage.global.util.HeaderUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // 모든 유저 반환
    @GetMapping
    private ResponseEntity<?> getFirstUser() {
        List<UserTestResponseDto> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원 비밀번호 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "비밀번호 변경 실패")
    })
    @PutMapping("/changedPassword")
    public ResponseEntity<?> changedPassword(
            HttpServletRequest request,
            @RequestBody UserChangedPwRequestDto userChangedPwRequestDto) {
        log.info("[UserController changedPassword()] password: {}, newPassword: {}",
                userChangedPwRequestDto.password(), userChangedPwRequestDto.newPassword());

        // Http Header 의 Authorization (Access Token) 추출
        String accessToken = HeaderUtil.getAccessToken(request);
        userService.changedPassword(accessToken, userChangedPwRequestDto);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "찜하기 / 찜취소")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "찜목록 추가"),
            @ApiResponse(responseCode = "500", description = "찜목록 추가 실패")
    })
    @PutMapping("/interest/{saleId}")
    public ResponseEntity<?> interest(
            HttpServletRequest request,
            @PathVariable Long saleId) {
        log.info("[UserController interest()] saleId: {}", saleId);

        // Http Header 의 Authorization (Access Token) 추출
        String accessToken = HeaderUtil.getAccessToken(request);

        // 찜목록 추가 및 삭제
        Map<String, Boolean> result = userService.interest(accessToken, saleId);

        return ResponseEntity.ok(result);
    }


    /**
     * 찜 목록 조회
     */

    /**
     * 예약
     */

    /**
     * 예약취소
     */

    /**
     * 예약 목록 조회
     */

    // 브로커 회원
    /**
     * 내 상품 목록
     */

    /**
     * 예약 현황 조회
     */


}
