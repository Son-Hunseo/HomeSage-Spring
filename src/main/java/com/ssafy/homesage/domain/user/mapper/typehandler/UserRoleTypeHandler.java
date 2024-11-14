package com.ssafy.homesage.domain.user.mapper.typehandler;

import com.ssafy.homesage.domain.user.model.enums.UserRole;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRoleTypeHandler extends BaseTypeHandler<UserRole> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UserRole parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getRole());  // UserRole의 문자열 값을 DB에 설정
    }

    @Override
    public UserRole getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String role = rs.getString(columnName);
        return role != null ? UserRole.valueOf(role.toUpperCase()) : null;  // DB 문자열을 Enum으로 변환
    }

    @Override
    public UserRole getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String role = rs.getString(columnIndex);
        return role != null ? UserRole.valueOf(role.toUpperCase()) : null;
    }

    @Override
    public UserRole getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String role = cs.getString(columnIndex);
        return role != null ? UserRole.valueOf(role.toUpperCase()) : null;
    }
}
