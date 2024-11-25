package com.ssafy.homesage.domain.user.service;

import com.ssafy.homesage.domain.sale.model.dto.SaleResponseDto;
import com.ssafy.homesage.domain.user.exception.*;
import com.ssafy.homesage.domain.user.mapper.AuthMapper;
import com.ssafy.homesage.domain.user.mapper.UserMapper;
import com.ssafy.homesage.domain.user.model.dto.*;
import com.ssafy.homesage.domain.user.model.entity.User;
import com.ssafy.homesage.global.util.HashUtil;
import com.ssafy.homesage.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final AuthMapper authMapper;
    private final HashUtil hashUtil;
    private final JwtUtil jwtUtil;

    @Override
    public List<UserTestResponseDto> getAllUsers() {

        List<User> allUsers = userMapper.findAllUsers();
        List<UserTestResponseDto> allUserInfo = new ArrayList<>();

        for (User user : allUsers) {
            allUserInfo.add(new UserTestResponseDto(user));
        }

        return allUserInfo;
    }

    @Override
    @Transactional
    public void changedPassword(String accessToken, UserChangedPwRequestDto userChangedPwRequestDto) {
        log.info("[UserService changedPassword()] accessToken: {}", accessToken);
        // 토큰에서 사용자의 이메일 추출
        String userEmail = jwtUtil.getUserEmail(accessToken, "AccessToken");

        // 사용자의 이메일을 통해 비밀번호 조회
        String findPassword = userMapper.findPasswordByEmail(userEmail);
        log.info("[UserService changedPassword()] findPassword: {}", findPassword);

        // 조회한 비밀번호와 입력 된 비밀번호가 일치하지 않으면 예외 처리
        String hashedPassword = hashUtil.getDigest(userChangedPwRequestDto.currentPassword());
        if (!findPassword.equals(hashedPassword)) {
            throw new MismatchPasswordException();
        }

        // 일치한다면 비밀번호 업데이트
        userMapper.updateNewPassword(userEmail, hashUtil.getDigest(userChangedPwRequestDto.newPassword()));

        // 로그아웃 처리
        authMapper.updateValidTokenToInvalidByUserEmail(userEmail);
    }

    @Override
    @Transactional
    public Map<String, Boolean> interest(String accessToken, Long saleId) {
        // userId 조회
        Long userId = findUserId(accessToken);

        // saleId 와 사용자 이메일을 통해 찜목록에 데이터가 있는지 확인
        int count = userMapper.findInterestBySaleIdAndUserId(saleId, userId);

        Map<String, Boolean> resultMap = new HashMap<>();
        if (count == 0) {
            // 없다면, 찜목록에 추가 후 map 에 true 를 담는다.
            userMapper.insertInterest(saleId, userId);
            resultMap.put("isInterest", true);
        } else {
            // 있다면, 찜목록에서 삭제 후 map 에 false 를 담는다.
            userMapper.deleteInterest(saleId, userId);
            resultMap.put("isInterest", false);
        }
        return resultMap;
    }

    @Override
    public List<InterestedSalesResponseDto> interestList(String accessToken) {
        // userId 조회
        Long userId = findUserId(accessToken);

        List<InterestedSalesResponseDto> interestedSalesResponseDtoList =
                userMapper.findAllUserInterestedSales(userId);

        // 조회 후 빈 List 라면 예외 처리
        if (interestedSalesResponseDtoList.isEmpty()) {
            throw new EmptyInterestedSalesException();
        }

        return interestedSalesResponseDtoList;
    }

    @Override
    @Transactional
    public void reservation(String accessToken, ReserveRequestDto reserveRequestDto) {
        // userId 조회
        Long userId = findUserId(accessToken);

        // 이미 있는 예약이면 예외처리 (같은 건물을 다른 시간대에 예약하려 하는 경우 등)
        int sameSaleCount = userMapper.findReserveByUserIdAndProviderUserIdAndSaleId(
                userId, reserveRequestDto.providerUserId(), reserveRequestDto.saleId());
        if (sameSaleCount != 0) {
            throw new DuplicateReservationException();
        }

        int sameTimeCount = userMapper.findReserveByUserIdAndReserveDateTime(
                userId, reserveRequestDto.reserveDate() + " " + reserveRequestDto.reserveTime());
        if (sameTimeCount != 0) {
            throw new DuplicateReservationException();
        }

        // 예약
        ReserveRequestDto insertReserveRequestDto = ReserveRequestDto.builder()
                .consumerUserId(userId)
                .providerUserId(reserveRequestDto.providerUserId())
                .saleId(reserveRequestDto.saleId())
                .reserveDateTime(reserveRequestDto.reserveDate() + " " + reserveRequestDto.reserveTime())
                .build();
        userMapper.insertReservation(insertReserveRequestDto);
    }

    @Override
    @Transactional
    public void cancelReserve(String accessToken, Long saleId) {
        // userId 조회
        Long userId = findUserId(accessToken);

        // 예약 취소
        userMapper.deleteByUserIdAndSaleId(userId, saleId);
    }

    @Override
    public List<ReserveResponseDto> reserveList(String accessToken) {
        // userId 조회
        Long userId = findUserId(accessToken);
        log.info("[UserService reserveList()] UserId: {}", userId);

        // 예약 목록 조회
        List<ReserveResponseDto> reserveResponseDtoList =
                userMapper.findAllReserveListByUserId(userId);

        // 조회 후 빈 리스트면 예외 처리
        if (reserveResponseDtoList.isEmpty()) {
            throw new EmptyReservesException();
        }

        return reserveResponseDtoList;
    }

    @Override
    public List<SaleResponseDto> providerSaleList(String accessToken) {
        // userId 조회
        Long userId = findUserId(accessToken);

        // 관리 중인 상품 목록 조회
        List<SaleResponseDto> saleResponseDtoList =
                userMapper.findAllSalesByProviderUserId(userId);

        // 조회 후 빈 리스트면 예외 처리
        if (saleResponseDtoList.isEmpty()) {
            throw new EmptyManageSalesException();
        }

        return saleResponseDtoList;
    }

    @Override
    public List<ReserveResponseDto> providerReserveList(String accessToken) {
        // userId 조회
        Long userId = findUserId(accessToken);
        log.info("[UserService providerReserveList()] UserId: {}", userId);

        // 예약 된 상품 목록 조회
        List<ReserveResponseDto> reserveResponseDtoList
                = userMapper.findAllReserveListByProviderUserId(userId);

        // 조회 후 빈 리스트면 예외 처리
        if (reserveResponseDtoList.isEmpty()) {
            throw new EmptyReservesException();
        }

        return reserveResponseDtoList;
    }

    /**
     * 토큰에서 사용자의 이메일을 추출 후 user_id 조회
     */
    private Long findUserId(String accessToken) {
        // 토큰에서 사용자의 이메일 추출
        String userEmail = jwtUtil.getUserEmail(accessToken, "AccessToken");

        // 이메일을 통해 userId 조회
        return userMapper.findIdByEmail(userEmail);
    }
}
