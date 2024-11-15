package com.ssafy.homesage.domain.user.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class UserInterestedSales {

    private Long userInterestedSaleId;
    private Long saleId;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
