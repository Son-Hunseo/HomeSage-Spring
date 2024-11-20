package com.ssafy.homesage.domain.ai.controller;

import com.ssafy.homesage.domain.ai.model.dto.AnalyzeInfoResponseDto;
import com.ssafy.homesage.domain.ai.model.dto.AnalyzeListResponseDto;
import com.ssafy.homesage.domain.ai.model.dto.AnalyzeResultResponseDto;
import com.ssafy.homesage.domain.ai.model.dto.CreateAnalyzeResponseDto;
import com.ssafy.homesage.domain.ai.service.AnalyzeService;
import com.ssafy.homesage.global.util.HeaderUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "등기부 등본, 건축물 대장 분석", description = "등기부 등본, 건축물 대장 분석")
@RestController
@RequestMapping("/analyze")
public class AnalyzeController {

    private final AnalyzeService analyzeService;

    /**
     * 분석 방 리스트 반환
     */
    @GetMapping
    public ResponseEntity<?> getAnalyzeList(HttpServletRequest request) {

        // Http Header 의 Authorization (Access Token) 추출
        String accessToken = HeaderUtil.getAccessToken(request);

        // 해당 유저의 email로 해당하는 분석 방 리스트 반환
        AnalyzeListResponseDto analyzeListResponseDto = analyzeService.getAnalyzeList(accessToken);

        return ResponseEntity.ok(analyzeListResponseDto);
    }

    /**
     * 새로운 분석 방 생성
     */
    @PostMapping
    public ResponseEntity<?> createAnalyze(HttpServletRequest request) {

        // Http Header 의 Authorization (Access Token) 추출
        String accessToken = HeaderUtil.getAccessToken(request);

        // 해당 유저의 email로 새로운 분석 방을 생성하고, 생성한 분석 방의 id 반환
        CreateAnalyzeResponseDto createAnalyzeResponseDto = analyzeService.createAnalyze(accessToken);

        return ResponseEntity.ok(createAnalyzeResponseDto);
    }

    /**
     * 분석방 입장
     * - 해당  analyze를 생성하지 않은 유저는 접근 불가
     */
    @GetMapping("/{analyze_id}")
    public ResponseEntity<?> getAnalyzeInfo(
            @PathVariable("analyze_id") int analyzeId,
            HttpServletRequest request) {

        // Http Header 의 Authorization (Access Token) 추출
        String accessToken = HeaderUtil.getAccessToken(request);

        // email로 해당 유저가 이 분석 방에 접근이 가능한지 여부를 반환
        boolean canAccess = analyzeService.checkCanAccessChatRoom(accessToken, analyzeId);

        if (canAccess) {
            // 정상적인 로직 진행
            AnalyzeInfoResponseDto analyzeInfoResponseDto = analyzeService.getAnalyzeInfo(analyzeId);

            return ResponseEntity.ok(analyzeInfoResponseDto);
        }

        // 권한 없음 오류 반환
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * 분석방 삭제
     */
    @DeleteMapping("/{analyze_id}")
    public ResponseEntity<?> deleteAnalyze(
            @PathVariable("analyze_id") int analyzeId,
            HttpServletRequest request) {

        // Http Header 의 Authorization (Access Token) 추출
        String accessToken = HeaderUtil.getAccessToken(request);

        // email로 해당 유저가 이 분석 방에 접근이 가능한지 여부를 반환
        boolean canAccess = analyzeService.checkCanAccessChatRoom(accessToken, analyzeId);

        if (canAccess) {
            // 정상적인 로직 진행
            analyzeService.deleteAnalyze(analyzeId);

            return ResponseEntity.ok().build();
        }

        // 권한 없음 오류 반환
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    /**
     * 등기부 등본 업로드
     */
    @PostMapping("/{analyze_id}/registered")
    public ResponseEntity<?> uploadRegistered(
            @PathVariable("analyze_id") int analyzeId,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {

        // Http Header 의 Authorization (Access Token) 추출
        String accessToken = HeaderUtil.getAccessToken(request);

        // email로 해당 유저가 이 분석 방에 접근이 가능한지 여부를 반환
        boolean canAccess = analyzeService.checkCanAccessChatRoom(accessToken, analyzeId);

        if (canAccess) {
            // 정상 로직 수행
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            String uploadDir = "/app/image/registered/";

            // 디렉토리가 없으면 생성
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            try {
                String originalFilename = file.getOriginalFilename();
                String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;
                Path path = Paths.get(uploadDir + uniqueFileName);

                Files.write(path, file.getBytes());

                // db에 url 저장
                analyzeService.saveRegisteredUrl(analyzeId, uniqueFileName);

                return ResponseEntity.ok().build();
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        }

        // 권한 없음 오류 반환
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * 건축물 대장 업로드
     */
    @PostMapping("/{analyze_id}/ledger")
    public ResponseEntity<?> uploadLedger(
            @PathVariable("analyze_id") int analyzeId,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {

        // Http Header 의 Authorization (Access Token) 추출
        String accessToken = HeaderUtil.getAccessToken(request);

        // email로 해당 유저가 이 분석 방에 접근이 가능한지 여부를 반환
        boolean canAccess = analyzeService.checkCanAccessChatRoom(accessToken, analyzeId);

        if (canAccess) {
            // 정상 로직 수행
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            String uploadDir = "/app/image/ledger/";

            // 디렉토리가 없으면 생성
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            try {
                String originalFilename = file.getOriginalFilename();
                String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;
                Path path = Paths.get(uploadDir + uniqueFileName);

                Files.write(path, file.getBytes());

                // db에 url 저장
                analyzeService.saveLedgerUrl(analyzeId, uniqueFileName);

                return ResponseEntity.ok().build();
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        }

        // 권한 없음 오류 반환
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * 분석 결과 받기
     */
    @PostMapping("/{analyze_id}")
    public ResponseEntity<?> getAnalyzeResult(
            @PathVariable("analyze_id") int analyzeId,
            HttpServletRequest request) throws ExecutionException, InterruptedException {

        // Http Header 의 Authorization (Access Token) 추출
        String accessToken = HeaderUtil.getAccessToken(request);

        // email로 해당 유저가 이 분석 방에 접근이 가능한지 여부를 반환
        boolean canAccess = analyzeService.checkCanAccessChatRoom(accessToken, analyzeId);

        if (canAccess) {
            // 정상 로직 수행
            CompletableFuture<AnalyzeResultResponseDto> analyzeResultResponseDto = analyzeService.getAnalyzeResult(analyzeId);

            return ResponseEntity.ok().body(analyzeResultResponseDto.get());
        }

        // 권한 없음 오류 반환
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
