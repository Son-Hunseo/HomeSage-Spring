package com.ssafy.homesage.domain.sale.model.dto;

import lombok.Builder;

@Builder
public record SaleResponseDto(
        Long saleId,
        Long providerUserId,
        String saleType,
        String homeType,
        Integer price,
        Integer monthlyFee,
        Integer managementFee,
        Double space,
        String description,
        String floor,
        String nearStation,
        String city,
        String gu,
        String dong,
        Double latitude,
        Double longitude,
        String cityGuDong
        ) {
}
