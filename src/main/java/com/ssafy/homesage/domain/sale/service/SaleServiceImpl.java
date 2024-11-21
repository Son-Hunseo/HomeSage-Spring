package com.ssafy.homesage.domain.sale.service;

import com.ssafy.homesage.domain.sale.exception.EmptySalesException;
import com.ssafy.homesage.domain.sale.mapper.SaleMapper;
import com.ssafy.homesage.domain.sale.model.dto.SaleResponseDto;
import com.ssafy.homesage.domain.sale.model.dto.SaleSearchCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class SaleServiceImpl implements SaleService {

    private final SaleMapper saleMapper;

    @Override
    public List<SaleResponseDto> searchSaleListByMapCenter(
            Double centerLat,
            Double centerLng,
            Double radius
    ) {
        // 지도 기반 매물 검색 로직
        // Haversine 공식 등을 활용한 반경 검색
        return saleMapper.findPropertiesWithinRadius(centerLat, centerLng, radius);
    }

    @Override
    public List<SaleResponseDto> searchSaleList(SaleSearchCondition searchCondition) {
        log.info("[SaleService searchSaleList()] searchCondition: {}", searchCondition);
        // 조회 전 Dto 처리
        initSearchCondition(searchCondition);

        // 검색 조건에 따른 데이터 조회
        List<SaleResponseDto> saleResponseDtoList = saleMapper.findBySearchCondition(searchCondition);

        // 조회 후 빈 리스트면 예외 처리
        if (saleResponseDtoList.isEmpty()) {
            throw new EmptySalesException();
        }

        return saleResponseDtoList;
    }

    @Override
    public SaleResponseDto saleDetail(Long saleId) {
        // 상품 상세 조회 및 예외처리
        return saleMapper.findById(saleId)
                .orElseThrow(() -> new EmptySalesException());
    }

    private void initSearchCondition(SaleSearchCondition searchCondition) {
        // 모든 필드가 null인지 확인
        boolean isAllNull = (searchCondition.getKeyword() == null || searchCondition.getKeyword().trim().isEmpty())
                        && (searchCondition.getSaleType() == null || searchCondition.getSaleType().trim().isEmpty())
                        && (searchCondition.getHomeType() == null || searchCondition.getHomeType().trim().isEmpty())
                        && (searchCondition.getStation() == null || searchCondition.getStation().trim().isEmpty())
                        && searchCondition.getMinPrice() == null
                        && searchCondition.getMaxPrice() == null
                        && searchCondition.getMinMonthlyFee() == null
                        && searchCondition.getMaxMonthlyFee() == null
                        && searchCondition.getMinSpace() == null
                        && searchCondition.getMaxSpace() == null;

        // 모든 값이 null일 경우 keyword를 "강남"으로 설정
        if (isAllNull) {
            searchCondition.setKeyword("강남");
        }
    }

}
