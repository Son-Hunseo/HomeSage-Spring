package com.ssafy.homesage.domain.user.mapper;

import com.ssafy.homesage.domain.user.model.dto.UserLoginRequestDto;
import com.ssafy.homesage.domain.user.model.dto.UserSignUpRequestDto;
import com.ssafy.homesage.domain.user.model.entity.User;
import com.ssafy.homesage.domain.user.model.jwt.Token;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.Optional;

@Mapper
public interface AuthMapper {

    /**
     * 회원가입
     */
    @Insert("""
            INSERT INTO users (email, password, name, role) 
            VALUES (#{email}, #{password}, #{name}, #{role})
            """)
    void saveUser(UserSignUpRequestDto signUpRequestDto);

    /**
     * 회원가입 시 중복 이메일인지 체크
     */
    @Select("""
            SELECT COUNT(*) 
            FROM users 
            WHERE email = #{email}
            """)
    int selectCountByEmail(String email);

    /**
     * 로그인 시 이메일과 비밀번호로 사용자 조회
     */
    @Select("""
        SELECT email, password, name, role, created_at, updated_at 
        FROM users
        WHERE email=#{email} AND password=#{password}
        """)
    Optional<User> findByEmailAndPassword(UserLoginRequestDto loginRequestDto);

        /**
         * 로그인 시 발급한 토큰을 저장
         */
        @Insert("""
        INSERT INTO token_status 
        VALUES(#{userEmail}, #{hashedToken}, #{dateExpiration}, 1)
        """)
        void saveToken(@Param("userEmail") String userEmail,
                       @Param("hashedToken") String hashedToken,
                       @Param("dateExpiration") Date dateExpiration);

    /**
     * 로그아웃 시 활성화 상태인 토큰을 비활성화 처리
     */
    @Update("""
            UPDATE token_status 
            SET valid = 0 
            WHERE user_email = #{userEmail} AND valid = 1
            """)
    int updateValidTokenToInvalidByUserEmail(String userEmail);

    /**
     * 새로운 토큰 발급 시, 기존의 토큰을 모두 비활성화 처리. (RefreshToken 은 제외)
     */
    @Update("""
            UPDATE token_status 
            SET valid = 0 
            WHERE user_email = #{userEmail} AND valid = 1 AND hashed_token != #{hashedRefreshToken}
            """)
    void updateValidTokenToInvalidByUserEmailAndRefreshToken(String userEmail, String hashedRefreshToken
    );

    /**
     * 토큰 내 userEmail 을 통해 사용자 조회
     */
    @Select("""
        SELECT * 
        FROM users 
        WHERE email=#{email}
        """)
    Optional<User> findByEmail(String email);

    /**
     * 해당 토큰이 활성화 토큰인지 조회
     */
    @Select("""
        SELECT COUNT(*) 
        FROM token_status 
        WHERE hashed_token = #{token} AND valid = 1
        """)
    boolean findValidStatusByToken(String token);
}
