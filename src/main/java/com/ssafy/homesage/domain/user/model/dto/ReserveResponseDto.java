package com.ssafy.homesage.domain.user.model.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReserveResponseDto(
        Long saleId,
        Long consumerUserId,
        Long providerUserId,
        LocalDateTime reservationDatetime,
        String saleType,
        String homeType,
        Integer price,
        Integer monthlyFee,
        Integer managementFee,
        Double space,
        String description,
        String floor,
        String nearStation
) {
}
