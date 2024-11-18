package com.ssafy.homesage.domain.sale.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class Sales {

    private Long saleId;
    private Long providerUserId;
    private String saleType;
    private String homeType;
    private Integer price;
    private Integer monthlyFee;
    private Integer managementFee;
    private Double space;
    private String description;
    private String floor;
    private String nearStation;
    private String city;
    private String gu;
    private String dong;
    private String cityGuDong;
    private Double latitude;
    private Double longitude;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
