package com.ssafy.homesage.domain.user.model.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record InterestedSalesResponse(Long UserInterestedSaleId, Long saleId, Long userId, LocalDateTime createdAt) {
}
