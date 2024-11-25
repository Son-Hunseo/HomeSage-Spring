package com.ssafy.homesage.domain.sale.controller;

import com.ssafy.homesage.domain.sale.model.dto.SaleResponseDto;
import com.ssafy.homesage.domain.sale.model.dto.SaleSearchCondition;
import com.ssafy.homesage.domain.sale.model.dto.SaleUploadRequestDto;
import com.ssafy.homesage.domain.sale.service.SaleService;
import com.ssafy.homesage.global.util.HeaderUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
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
    public ResponseEntity<List<SaleResponseDto>> searchByMap(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam Double radius
    ) {
        try {
            List<SaleResponseDto> results = saleService.searchByMapCenter(lat, lng, radius);
            log.info("result.size: {}", results.size());
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

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

    @Operation(summary = "매물의 예약 완료 된 시간 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "매물 정보 조회 성공"),
            @ApiResponse(responseCode = "500", description = "매물 정보 조회 실패")
    })
    @GetMapping("/reserve/{saleId}")
    public ResponseEntity<?> reserveSaleTime(@PathVariable Long saleId) {
        List<LocalDateTime> reserveTime = saleService.reserveSaleTime(saleId);

        return ResponseEntity.ok(reserveTime);
    }

    @Operation(summary = "PROVIDER - 상품 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "상품 등록 실패")
    })
    @PostMapping(value = "/provider/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saleUpload(
            @Valid @RequestPart("data") SaleUploadRequestDto request,
            @RequestPart("file") MultipartFile file,
            HttpServletRequest httpRequest) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String accessToken = HeaderUtil.getAccessToken(httpRequest);
        saleService.uploadSaleWithImage(request, file, accessToken);
        return ResponseEntity.ok().build();
    }
}