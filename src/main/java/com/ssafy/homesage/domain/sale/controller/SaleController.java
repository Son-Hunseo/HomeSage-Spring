package com.ssafy.homesage.domain.sale.controller;

import com.ssafy.homesage.domain.sale.model.dto.SaleResponseDto;
import com.ssafy.homesage.domain.sale.model.dto.SaleSearchCondition;
import com.ssafy.homesage.domain.sale.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "상품 컨트롤러", description = "상품 조회, 상세 조회")
@RestController
@RequestMapping("/sales")
public class SaleController {

    private final SaleService saleService;

    @Operation(summary = "매물 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 목록 조회 성공"),
            @ApiResponse(responseCode = "204", description = "상품 목록이 없습니다.")
    })
    @GetMapping("/list")
    public ResponseEntity<?> saleList(SaleSearchCondition searchConditionDto) {

        List<SaleResponseDto> saleResponseDtoList =
                saleService.searchSaleList(searchConditionDto);

        return ResponseEntity.ok().body(saleResponseDtoList);
    }

    @Operation(summary = "매물 조회 (지도)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 목록 조회 성공"),
            @ApiResponse(responseCode = "204", description = "상품 목록이 없습니다.")
    })
    @GetMapping("/map-search")
    public ResponseEntity<?> mapSaleList(
            @RequestParam Double centerLat,
            @RequestParam Double centerLng,
            @RequestParam(defaultValue = "1.0") Double radius
    ) {
        try {
            // 입력값 검증
            if (centerLat == null || centerLng == null || radius == null) {
                return ResponseEntity.badRequest().build();
            }

            List<SaleResponseDto> saleResponseDtoList =
                    saleService.searchSaleListByMapCenter(centerLat, centerLng, radius);

            return saleResponseDtoList.isEmpty()
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.ok().body(saleResponseDtoList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 상세 조회
     */
    @Operation(summary = "매물 상세 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 목록 조회 성공"),
            @ApiResponse(responseCode = "204", description = "상품 목록이 없습니다.")
    })
    @GetMapping("/detail/{saleId}")
    public ResponseEntity<?> saleDetail(@PathVariable Long saleId) {

        SaleResponseDto saleResponseDto =
                saleService.saleDetail(saleId);

        return ResponseEntity.ok().body(saleResponseDto);
    }
}
