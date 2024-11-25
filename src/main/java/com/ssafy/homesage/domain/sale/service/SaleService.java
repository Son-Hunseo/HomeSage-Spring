package com.ssafy.homesage.domain.sale.service;

import com.ssafy.homesage.domain.sale.model.dto.SaleResponseDto;
import com.ssafy.homesage.domain.sale.model.dto.SaleSearchCondition;
import com.ssafy.homesage.domain.sale.model.dto.SaleUploadRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleService {
    List<SaleResponseDto> searchSaleList(SaleSearchCondition searchConditionDto);

    SaleResponseDto saleDetail(Long saleId);

    List<SaleResponseDto> searchByMapCenter(Double lat, Double lng, Double radius);

    void uploadSaleWithImage(SaleUploadRequestDto saleUploadRequestDto, MultipartFile file, String accessToken);

    List<LocalDateTime> reserveSaleTime(Long saleId);
}
