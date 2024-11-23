package com.ssafy.homesage.domain.sale.mapper;

import com.ssafy.homesage.domain.sale.model.dto.SaleSearchCondition;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.SQL;

@Slf4j
public class SaleSqlProvider {
    public String findBySearchCondition(SaleSearchCondition searchCondition) {
        log.info("[SaleSqlProvider findBySearchCondition()] searchCondition: {}", searchCondition);

        return new SQL() {{
            SELECT("sale_id AS saleId");
            SELECT("provider_user_id AS providerUserId");
            SELECT("sale_type AS saleType");
            SELECT("home_type AS homeType");
            SELECT("price");
            SELECT("monthly_fee AS monthlyFee");
            SELECT("management_fee AS managementFee");
            SELECT("space");
            SELECT("description");
            SELECT("floor");
            SELECT("near_station AS nearStation");
            SELECT("city");
            SELECT("gu");
            SELECT("dong");
            SELECT("latitude");
            SELECT("longitude");
            SELECT("city_gu_dong AS cityGuDong");

            FROM("sales");

            if (searchCondition.getKeyword() != null && !searchCondition.getKeyword().trim().isEmpty()) {
                String keyword = searchCondition.getKeyword();
                WHERE("(city_gu_dong LIKE CONCAT('%', #{keyword}, '%') " +
                        "OR near_station LIKE CONCAT('%', #{keyword}, '%') " +
                        "OR description LIKE CONCAT('%', #{keyword}, '%'))");
            }

            if (searchCondition.getSaleType() != null && !searchCondition.getSaleType().trim().isEmpty()) {
                WHERE("sale_type = #{saleType}");
            }

            if (searchCondition.getHomeType() != null && !searchCondition.getHomeType().trim().isEmpty()) {
                WHERE("home_type = #{homeType}");
            }

            if (searchCondition.getMinPrice() != null) {
                WHERE("price >= #{minPrice}");
            }
            if (searchCondition.getMaxPrice() != null) {
                WHERE("price <= #{maxPrice}");
            }

            if (searchCondition.getMinMonthlyFee() != null) {
                WHERE("monthly_fee >= #{minMonthlyFee}");
            }
            if (searchCondition.getMaxMonthlyFee() != null) {
                WHERE("monthly_fee <= #{maxMonthlyFee}");
            }

            if (searchCondition.getMinSpace() != null) {
                WHERE("space >= #{minSpace}");
            }
            if (searchCondition.getMaxSpace() != null) {
                WHERE("space <= #{maxSpace}");
            }

            if (searchCondition.getStation() != null && !searchCondition.getStation().trim().isEmpty()) {
                WHERE("near_station LIKE CONCAT('%', #{station}, '%')");
            }

            ORDER_BY("created_at DESC");
        }}.toString();
    }
}
