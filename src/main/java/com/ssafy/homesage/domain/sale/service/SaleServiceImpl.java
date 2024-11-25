package com.ssafy.homesage.domain.sale.service;

import com.ssafy.homesage.domain.sale.exception.EmptySalesException;
import com.ssafy.homesage.domain.sale.mapper.SaleMapper;
import com.ssafy.homesage.domain.sale.model.dto.*;
import com.ssafy.homesage.domain.user.exception.UserNotFoundException;
import com.ssafy.homesage.domain.user.exception.UserNotProviderException;
import com.ssafy.homesage.domain.user.mapper.UserMapper;
import com.ssafy.homesage.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class SaleServiceImpl implements SaleService {

    private final SaleMapper saleMapper;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    @Value("${static.server.domain}")
    private String staticServerDomain;
    private final String uploadDir = "/app/image/sale/";

    @Override
    public List<SaleResponseDto> searchSaleList(SaleSearchCondition searchCondition) {
        log.info("[SaleService searchSaleList()] searchCondition: {}", searchCondition);
        // 조회 전 Dto 처리
        initSearchCondition(searchCondition);

        // 검색 조건에 따른 데이터 조회
        List<SaleResponseDto> saleResponseDtoList = saleMapper.findBySearchCondition(searchCondition);

        // 조회 후 빈 리스트면 예외 처리
        if (saleResponseDtoList.isEmpty()) {
            throw new EmptySalesException();
        }

        return saleResponseDtoList;
    }

    @Override
    public SaleResponseDto saleDetail(Long saleId) {
        // 상품 상세 조회 및 예외처리
        return saleMapper.findById(saleId)
                .orElseThrow(() -> new EmptySalesException());
    }

    @Override
    public List<SaleResponseDto> searchByMapCenter(Double lat, Double lng, Double radius) {
        return saleMapper.findByMapCenter(lat, lng, radius);
    }

    @Override
    public void uploadSaleWithImage(
            SaleUploadRequestDto saleUploadRequestDto,
            MultipartFile file,
            String accessToken) {

        // 토큰에서 사용자의 이메일 추출
        String userEmail = jwtUtil.getUserEmail(accessToken, "AccessToken");

        // 이메일을 통해 userId 조회
        Long userId = userMapper.findIdByEmail(userEmail);
        int isProvider = userMapper.findRoleByUserId(userId);

        if (isProvider != 0) {
            // 이미지 업로드
            String imageUrl = null;
            try {
                imageUrl = uploadImage(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            saleUploadRequestDto.setSaleImgUrl(staticServerDomain + "/static/sale/" + imageUrl);
            saleUploadRequestDto.setProviderUserId(userId);
            saleUploadRequestDto.setCityGuDong(saleUploadRequestDto.generateCityGuDong());

            // 매물 정보 저장
            saleMapper.insertSale(saleUploadRequestDto);
            return;
        }

        throw new UserNotProviderException();
    }

    @Override
    public List<LocalDateTime> reserveSaleTime(Long saleId) {
        List<LocalDateTime> reserveTime = saleMapper.findReservationDatetimeBySaleId(saleId);

        if (reserveTime.isEmpty()) {
            throw new EmptySalesException();
        }

        return reserveTime;
    }

    /**
     * 이미지 파일 이름 세팅
     */
    private String uploadImage(MultipartFile file) throws IOException {
        // 디렉토리 생성
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 파일명 생성 및 저장
        String originalFilename = file.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;
        Path path = Paths.get(uploadDir + uniqueFileName);

        Files.write(path, file.getBytes());

        return uniqueFileName;
    }

    /**
     * 검색 조건 초기화
     */
    private void initSearchCondition(SaleSearchCondition searchCondition) {
        // 모든 필드가 null인지 확인
        boolean isAllNull = (searchCondition.getKeyword() == null || searchCondition.getKeyword().trim().isEmpty())
                        && (searchCondition.getSaleType() == null || searchCondition.getSaleType().trim().isEmpty())
                        && (searchCondition.getHomeType() == null || searchCondition.getHomeType().trim().isEmpty())
                        && (searchCondition.getStation() == null || searchCondition.getStation().trim().isEmpty())
                        && searchCondition.getMinPrice() == null
                        && searchCondition.getMaxPrice() == null
                        && searchCondition.getMinMonthlyFee() == null
                        && searchCondition.getMaxMonthlyFee() == null
                        && searchCondition.getMinSpace() == null
                        && searchCondition.getMaxSpace() == null;

        // 모든 값이 null일 경우 keyword를 "강남"으로 설정
        if (isAllNull) {
            searchCondition.setKeyword("서울");
        }
    }
}
