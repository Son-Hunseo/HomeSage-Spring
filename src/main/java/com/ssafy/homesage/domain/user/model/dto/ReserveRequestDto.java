package com.ssafy.homesage.domain.user.model.dto;

import lombok.Builder;

@Builder
public record ReserveRequestDto(
        Long consumerUserId,
        Long providerUserId,
        Long saleId,
        String reserveDate,
        String reserveTime,
        String reserveDateTime
) {
}
