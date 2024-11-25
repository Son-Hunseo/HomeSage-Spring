package com.ssafy.homesage.domain.sale.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleUploadRequestDto {

    @NotNull(message = "매물 종류는 필수입니다")
    private String saleType;

    private Long providerUserId;

    @NotNull(message = "건물 종류는 필수입니다")
    private String homeType;

    @NotNull(message = "가격은 필수입니다")
    private Integer price;

    private Integer monthlyFee;

    @NotNull(message = "관리비는 필수입니다")
    private Integer managementFee;

    @NotNull(message = "면적은 필수입니다")
    private Double space;

    @NotEmpty(message = "설명은 필수입니다")
    private String description;

    @NotEmpty(message = "층수는 필수입니다")
    private String floor;

    private String nearStation;

    @NotEmpty(message = "시는 필수입니다")
    private String city;

    @NotEmpty(message = "구는 필수입니다")
    private String gu;

    @NotEmpty(message = "동은 필수입니다")
    private String dong;

    @NotNull(message = "위도는 필수입니다")
    private Double latitude;

    @NotNull(message = "경도는 필수입니다")
    private Double longitude;

    private String cityGuDong;
    private String saleImgUrl;

    public String generateCityGuDong() {
        return String.format("%s %s %s", city, gu, dong);
    }
}