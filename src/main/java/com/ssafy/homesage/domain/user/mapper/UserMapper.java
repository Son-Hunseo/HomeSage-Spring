package com.ssafy.homesage.domain.user.mapper;

import java.time.LocalDateTime;
import java.util.List;

import com.ssafy.homesage.domain.user.model.dto.InterestedSalesResponseDto;
import com.ssafy.homesage.domain.user.model.dto.ReserveRequestDto;
import com.ssafy.homesage.domain.user.model.dto.ReserveResponseDto;
import com.ssafy.homesage.domain.user.model.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    List<User> findAllUsers();

    /**
     * 비밀번호 변경 시 기존 비밀번호 조회 (입력한 비밀번호와 일치여부 확인을 위해)
     */
    @Select("""
        SELECT password
        FROM users
        WHERE email = #{userEmail}
    """)
    String findPasswordByEmail(String userEmail);

    /**
     * 새로 입력한 비밀번호로 변경
     */
    @Update("""
        UPDATE users
        SET password = #{newPassword}
        WHERE email = #{userEmail}
    """)
    void updateNewPassword(String userEmail, String newPassword);

    /**
     * 토큰 내 email 을 통해 userId 를 조회해온다.
     */
    @Select("""
        SELECT user_id
        FROM users
        WHERE email = #{userEmail}
    """)
    Long findIdByEmail(String userEmail);

    /**
     * 찜목록에 데이터가 있는지 확인한다.
     */
    @Select("""
        SELECT COUNT(*)
        FROM user_interested_sales
        WHERE sale_id = #{saleId} AND user_id = #{userId}
    """)
    int findInterestBySaleIdAndUserId(Long saleId, Long userId);

    /**
     * 찜목록에 추가
     */
    @Insert("""
        INSERT INTO user_interested_sales (sale_id, user_id)
        VALUES (#{saleId}, #{userId})
    """)
    void insertInterest(Long saleId, Long userId);

    /**
     * 찜목록에서 삭제
     */
    @Delete("""
        DELETE FROM user_interested_sales
        WHERE sale_id = #{saleId} AND user_id = #{userId}
    """)
    void deleteInterest(Long saleId, Long userId);

    /**
     * 찜 목록 조회
     */
    @Select("""
        SELECT uis.user_interested_sale_id, uis.sale_id, uis.user_id, 
        s.provider_user_id, s.sale_type, s.home_type, s.price, s.monthly_fee, 
        s.management_fee, s.space, s.floor, s.near_station 
        FROM user_interested_sales uis
        LEFT JOIN sales s ON uis.sale_id = s.sale_id 
        WHERE user_id = #{userId}
    """)
    List<InterestedSalesResponseDto> findAllUserInterestedSales(Long userId);

    /**
     * 중복 예약 조회
     */
    @Select("""
        SELECT COUNT(*)
        FROM reservations
        WHERE consumer_user_id = #{userId} AND provider_user_id = #{providerUserId} AND sale_id = #{saleId}
    """)
    int findReserveByUserIdAndProviderUserIdAndSaleId(Long userId, Long providerUserId, Long saleId);

    /**
     * 예약
     */
    @Insert("""
        INSERT INTO reservations (consumer_user_id, provider_user_id, sale_id, reservation_datetime)
        VALUES (#{consumerUserId}, #{providerUserId}, #{saleId}, #{reserveDateTime})
    """)
    void insertReservation(ReserveRequestDto reserveRequestDto);

    /**
     * 예약 취소
     */
    @Delete("""
        DELETE FROM reservations
        WHERE consumer_user_id = #{userId} AND sale_id = #{saleId}
    """)
    void deleteByUserIdAndSaleId(Long userId, Long saleId);

    /**
     * 소비자의 예약 목록 조회
     */
    @Select("""
        SELECT r.sale_id, r.consumer_user_id, r.provider_user_id, r.reservation_datetime,
        s.sale_type, s.home_type, s.price, s.monthly_fee, 
        s.management_fee, s.space, s.floor, s.near_station
        FROM reservations r
        LEFT JOIN sales s ON r.sale_id = s.sale_id
        WHERE r.consumer_user_id = #{userId}
    """)
    List<ReserveResponseDto> findAllReserveListByUserId(Long userId);
}