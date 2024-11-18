package com.ssafy.homesage.domain.sale.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleSearchCondition {

    private String keyword;
    private String saleType;
    private String homeType;
    private Integer minPrice;
    private Integer maxPrice;
    private Integer minMonthlyFee;
    private Integer maxMonthlyFee;
    private Double minSpace;
    private Double maxSpace;
    private String station;
}
