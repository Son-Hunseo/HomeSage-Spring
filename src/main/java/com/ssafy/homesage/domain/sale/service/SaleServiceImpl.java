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
    public List<SaleResponseDto> searchSaleList(SaleSearchCondition searchCondition) {
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
        // keyword 가 null 이라면 default: 서울시 강남구 역삼동으로 설정
        if (searchCondition.getKeyword() == null || searchCondition.getKeyword().trim().isEmpty()) {
            searchCondition.setKeyword("강남");
        } else {
            searchCondition.setKeyword(searchCondition.getKeyword().trim());
        }
    }
}
