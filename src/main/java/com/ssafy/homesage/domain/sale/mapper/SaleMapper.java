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
            s.sale_id as saleId,
            s.provider_user_id as providerUserId,
            u.name as providerUserName,
            s.sale_type as saleType,
            s.home_type as homeType,
            s.price,
            s.monthly_fee as monthlyFee,
            s.management_fee as managementFee,
            s.space,
            s.description,
            s.floor,
            s.near_station as nearStation,
            s.city,
            s.gu,
            s.dong,
            s.latitude,
            s.longitude,
            s.city_gu_dong as cityGuDong,
            s.sale_img_url as saleImgUrl
        FROM sales s
        LEFT JOIN users u ON s.provider_user_id = u.user_id 
        WHERE sale_id = #{saleId}
    """)
    Optional<SaleResponseDto> findById(Long saleId);

    @Select("""
        SELECT 
            s.sale_id as saleId,
            s.provider_user_id as providerUserId,
            u.name as providerUserName,
            s.sale_type as saleType,
            s.home_type as homeType,
            s.price as price,
            s.monthly_fee as monthlyFee,
            s.management_fee as managementFee,
            s.space as space,
            s.description as description,
            s.floor as floor,
            s.near_station as nearStation,
            s.city as city,
            s.gu as gu,
            s.dong as dong,
            s.latitude as latitude,
            s.longitude as longitude,
            s.city_gu_dong as cityGuDong,
            s.sale_img_url as saleImgUrl,
            ST_Distance_Sphere(
                point(s.longitude, s.latitude),
                point(#{lng}, #{lat})
            ) as distance
        FROM sales s
        LEFT JOIN users u ON s.provider_user_id = u.user_id
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
