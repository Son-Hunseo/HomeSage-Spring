package com.ssafy.homesage.domain.sale.mapper;

import com.ssafy.homesage.domain.sale.model.dto.SaleSearchCondition;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.SQL;

@Slf4j
public class SaleSqlProvider {

    public String findBySearchCondition(SaleSearchCondition searchCondition) {
        log.info("[SaleSqlProvider findBySearchCondition()] searchCondition: {}", searchCondition);

        return new SQL() {{
            SELECT("sale_id AS saleId, provider_user_id AS providerUserId, sale_type AS saleType, " +
                    "home_type AS homeType, price, monthly_fee AS monthlyFee, management_fee AS managementFee, " +
                    "space, description, floor, near_station AS nearStation, city, gu, dong, latitude, " +
                    "longitude, city_gu_dong AS cityGuDong, created_at AS createdAt, updated_at AS updatedAt");
            FROM("sales");

            // Keyword 검색
            if (searchCondition.getKeyword() != null && !searchCondition.getKeyword().trim().isEmpty()) {
                WHERE("(city_gu_dong LIKE CONCAT('%', #{keyword}, '%') " +
                        "OR description LIKE CONCAT('%', #{keyword}, '%'))");
            }

            // 매물 유형 필터링
            if (searchCondition.getSaleType() != null && !searchCondition.getSaleType().trim().isEmpty()) {
                WHERE("sale_type = #{saleType}");
            }

            // 주택 유형 필터링
            if (searchCondition.getHomeType() != null && !searchCondition.getHomeType().trim().isEmpty()) {
                WHERE("home_type = #{homeType}");
            }

            // 가격 필터링
            if (searchCondition.getMinPrice() != null) {
                WHERE("price >= #{minPrice}");
            }
            if (searchCondition.getMaxPrice() != null) {
                WHERE("price <= #{maxPrice}");
            }

            // 월세 필터링
            if (searchCondition.getMinMonthlyFee() != null) {
                WHERE("monthly_fee >= #{minMonthlyFee}");
            }
            if (searchCondition.getMaxMonthlyFee() != null) {
                WHERE("monthly_fee <= #{maxMonthlyFee}");
            }

            // 면적 필터링
            if (searchCondition.getMinSpace() != null) {
                WHERE("space >= #{minSpace}");
            }
            if (searchCondition.getMaxSpace() != null) {
                WHERE("space <= #{maxSpace}");
            }

            // 역 필터링
            if (searchCondition.getStation() != null && !searchCondition.getStation().trim().isEmpty()) {
                WHERE("near_station = #{station}");
            }
        }}.toString();
    }
}
