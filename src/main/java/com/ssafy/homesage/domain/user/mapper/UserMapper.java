package com.ssafy.homesage.domain.user.mapper;

import java.util.List;

import com.ssafy.homesage.domain.user.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
}