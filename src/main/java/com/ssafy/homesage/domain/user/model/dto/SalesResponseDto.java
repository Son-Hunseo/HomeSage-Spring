package com.ssafy.homesage.domain.user.model.dto;

import lombok.Builder;

@Builder
public record SalesResponseDto(
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
        String nearStation) {
}
