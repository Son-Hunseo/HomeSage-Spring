package com.ssafy.homesage.domain.sale.mapper;

import com.ssafy.homesage.domain.sale.model.dto.SaleResponseDto;
import com.ssafy.homesage.domain.sale.model.dto.SaleSearchCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SaleMapper {

    /**
     * 검색 조건에 따른 조회 (조건이 없다면 '강남' 을 기준으로 전체 조회)
     */
    @SelectProvider(type = SaleSqlProvider.class, method = "findBySearchCondition")
    List<SaleResponseDto> findBySearchCondition(SaleSearchCondition searchCondition);

    @Select("""
                SELECT sale_id, provider_user_id, sale_type, home_type, price, monthly_fee, management_fee,
                space, description, floor, near_station, city, gu, dong, latitude, longitude, city_gu_dong
                FROM sales
                WHERE sale_id = #{saleId}
            """)
    Optional<SaleResponseDto> findById(Long saleId);


    @Select("""
            SELECT 
                sale_id as saleId,
                CAST(latitude AS DECIMAL(10,6)) as latitude,
                CAST(longitude AS DECIMAL(10,6)) as longitude,
                city_gu_dong as cityGuDong,
                (6371 * acos(
                    CASE 
                        WHEN cos(radians(#{centerLat})) * cos(radians(latitude)) * 
                             cos(radians(longitude) - radians(#{centerLng})) + 
                             sin(radians(#{centerLat})) * sin(radians(latitude)) > 1 THEN 1
                        WHEN cos(radians(#{centerLat})) * cos(radians(latitude)) * 
                             cos(radians(longitude) - radians(#{centerLng})) + 
                             sin(radians(#{centerLat})) * sin(radians(latitude)) < -1 THEN -1
                        ELSE cos(radians(#{centerLat})) * cos(radians(latitude)) * 
                             cos(radians(longitude) - radians(#{centerLng})) + 
                             sin(radians(#{centerLat})) * sin(radians(latitude))
                    END
                )) as distance
            FROM sales
            WHERE latitude IS NOT NULL
              AND longitude IS NOT NULL
            HAVING distance <= #{radius}
            ORDER BY distance
            """)
    List<SaleResponseDto> findSalesWithinRadius(@Param("centerLat") double centerLat,
                                                @Param("centerLng") double centerLng,
                                                @Param("radius") double radius);

}
