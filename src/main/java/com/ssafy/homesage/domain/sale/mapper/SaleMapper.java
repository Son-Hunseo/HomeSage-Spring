package com.ssafy.homesage.domain.sale.mapper;

import com.ssafy.homesage.domain.sale.model.dto.SaleMapSearchCondition;
import com.ssafy.homesage.domain.sale.model.dto.SaleResponseDto;
import com.ssafy.homesage.domain.sale.model.dto.SaleSearchCondition;
import com.ssafy.homesage.domain.sale.model.dto.SaleUploadRequestDto;
import org.apache.ibatis.annotations.*;

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
        SELECT 
            sale_id as saleId,
            provider_user_id as providerUserId,
            sale_type as saleType,
            home_type as homeType,
            price,
            monthly_fee as monthlyFee,
            management_fee as managementFee,
            space,
            description,
            floor,
            near_station as nearStation,
            city,
            gu,
            dong,
            latitude,
            longitude,
            city_gu_dong as cityGuDong,
            sale_img_url as saleImgUrl,
        FROM sales
        WHERE sale_id = #{saleId}
    """)
    Optional<SaleResponseDto> findById(Long saleId);

    @Select("""
                SELECT 
                    s.sale_id,
                    s.provider_user_id,
                    s.sale_type,
                    s.home_type,
                    s.price,
                    s.monthly_fee,
                    s.management_fee,
                    s.space,
                    s.description,
                    s.floor,
                    s.near_station,
                    s.city,
                    s.gu,
                    s.dong,
                    s.latitude,
                    s.longitude,
                    CONCAT(s.city, ' ', s.gu, ' ', s.dong) as city_gu_dong,
                    ST_Distance_Sphere(
                        point(s.longitude, s.latitude),
                        point(#{lng}, #{lat})
                    ) as distance
                FROM sales s
                WHERE ST_Distance_Sphere(
                    point(s.longitude, s.latitude),
                    point(#{lng}, #{lat})
                ) <= #{radius}
                ORDER BY distance ASC
            """)
    List<SaleResponseDto> findByMapCenter(
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radius") Double radius);

    @Insert("""
        INSERT INTO sales (
            provider_user_id,
            sale_type,
            home_type,
            price,
            monthly_fee,
            management_fee,
            space,
            description,
            floor,
            near_station,
            city,
            gu,
            dong,
            latitude,
            longitude,
            city_gu_dong,
            sale_img_url
        ) VALUES (
            #{providerUserId},
            #{saleType},
            #{homeType},
            #{price},
            #{monthlyFee},
            #{managementFee},
            #{space},
            #{description},
            #{floor},
            #{nearStation},
            #{city},
            #{gu},
            #{dong},
            #{latitude},
            #{longitude},
            #{cityGuDong},
            #{saleImgUrl}
        )
    """)
    void insertSale(SaleUploadRequestDto saleUploadRequestDto);
}
