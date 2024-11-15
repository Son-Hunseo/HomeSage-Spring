package com.ssafy.homesage.domain.user.model.dto;

import lombok.Builder;

@Builder
public record InterestedSalesResponse(
        Long UserInterestedSaleId,
        Long saleId,
        Long userId,
        Long providerUserId,
        String saleType,
        String homeType,
        Integer price,
        Integer monthlyFee,
        Integer managementFee,
        Double space,
        String floor,
        String nearStation) {
}
