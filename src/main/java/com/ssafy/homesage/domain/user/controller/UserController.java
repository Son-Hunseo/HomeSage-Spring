package com.ssafy.homesage.domain.user.controller;

import java.util.List;
import java.util.Map;

import com.ssafy.homesage.domain.sale.model.dto.SaleResponseDto;
import com.ssafy.homesage.domain.user.model.dto.*;
import com.ssafy.homesage.domain.user.service.UserService;
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
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "회원 기능 컨트롤러", description = "마이페이지, 찜, 예약")
@RestController
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
        log.info("[UserController changedPassword()] currentPassword: {}, newPassword: {}, confirmPassword: {}",
                userChangedPwRequestDto.currentPassword(),
                userChangedPwRequestDto.newPassword(),
                userChangedPwRequestDto.confirmPassword());

        // Http Header 의 Authorization (Access Token) 추출
        String accessToken = HeaderUtil.getAccessToken(request);
        userService.changedPassword(accessToken, userChangedPwRequestDto);

        // RefreshToken 삭제
        HttpHeaders httpHeaders = new HttpHeaders();
        ResponseCookie responseCookie = ResponseCookie
                .from(HeaderUtil.getRefreshCookieName(), "")
                .domain("homesage.my")
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(0)
                .sameSite("None")
                .build();

        return ResponseEntity.noContent()
                .headers(httpHeaders).header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .build();
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

    @Operation(summary = "찜목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "찜목록 반환"),
            @ApiResponse(responseCode = "204", description = "찜목록이 없습니다.")
    })
    @GetMapping("/interest/list")
    public ResponseEntity<?> interestList(
            HttpServletRequest request) {
        // Http Header 의 Authorization (Access Token) 추출
        String accessToken = HeaderUtil.getAccessToken(request);

        // 찜목록 조회
        List<InterestedSalesResponseDto> interestedSalesResponseDtoList = userService.interestList(accessToken);

        return ResponseEntity.ok(interestedSalesResponseDtoList);
    }

    @Operation(summary = "예약하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "예약 완료"),
            @ApiResponse(responseCode = "500", description = "이미 예약되어있습니다.")
    })
    @PostMapping("/reserve")
    public ResponseEntity<?> reservation(
            HttpServletRequest request,
            @RequestBody ReserveRequestDto reserveRequestDto) {
        // Http Header 의 Authorization (Access Token) 추출
        String accessToken = HeaderUtil.getAccessToken(request);

        userService.reservation(accessToken, reserveRequestDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "예약취소")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 취소 성공"),
            @ApiResponse(responseCode = "500", description = "이미 취소 된 예약입니다.")
    })
    @DeleteMapping("/cancel/{saleId}")
    public ResponseEntity<?> cancelReserve(
            HttpServletRequest request,
            @PathVariable Long saleId) {
        log.info("[UserController cancelReserve()] saleId: {}", saleId);

        // Http Header 의 Authorization (Access Token) 추출
        String accessToken = HeaderUtil.getAccessToken(request);

        userService.cancelReserve(accessToken, saleId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "예약 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 목록 조회 성공"),
            @ApiResponse(responseCode = "204", description = "예약 목록이 없습니다.")
    })
    @GetMapping("/reserve/list")
    public ResponseEntity<?> reserveList(
            HttpServletRequest request) {
        // Http Header 의 Authorization (Access Token) 추출
        String accessToken = HeaderUtil.getAccessToken(request);

        // 찜목록 조회
        List<ReserveResponseDto> reserveResponseDtoList = userService.reserveList(accessToken);

        return ResponseEntity.ok(reserveResponseDtoList);
    }

    // 브로커 회원
    @Operation(summary = "내가 관리하는 상품목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 목록 조회 성공"),
            @ApiResponse(responseCode = "204", description = "관리 중인 목록이 없습니다.")
    })
    @GetMapping("/provider/sale/list")
    public ResponseEntity<?> providerSaleList(
            HttpServletRequest request) {
        // Http Header 의 Authorization (Access Token) 추출
        String accessToken = HeaderUtil.getAccessToken(request);

        // 상품 목록 조회
        List<SaleResponseDto> saleResponseDtoList =
                userService.providerSaleList(accessToken);

        return ResponseEntity.ok(saleResponseDtoList);
    }

    /**
     * 예약 현황 조회
     */
    @Operation(summary = "예약 된 상품목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "판매자 예약 목록 조회 성공"),
            @ApiResponse(responseCode = "204", description = "예약 완료 된 목록이 없습니다.")
    })
    @GetMapping("/provider/reserve/list")
    public ResponseEntity<?> providerReserveList(
            HttpServletRequest request) {
        // Http Header 의 Authorization (Access Token) 추출
        String accessToken = HeaderUtil.getAccessToken(request);

        // 상품 목록 조회
        List<ReserveResponseDto> reserveResponseDtoList =
                userService.providerReserveList(accessToken);

        return ResponseEntity.ok(reserveResponseDtoList);
    }
}
