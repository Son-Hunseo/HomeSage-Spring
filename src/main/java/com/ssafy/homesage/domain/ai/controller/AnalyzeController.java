package com.ssafy.homesage.domain.ai.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "등기부 등본, 건축물 대장 분석", description = "등기부 등본, 건축물 대장 분석")
@RestController
@RequestMapping("/analyze")
public class AnalyzeController {

    @Value("${server.ip}")
    private String serverIp;

    @Value("${static.port}")
    private String staticPort;

    /**
     * 등기부 등본 업로드
     */
//    @PostMapping("/{analyze_id}/registered")
    @PostMapping("/registered")
    public ResponseEntity<?> uploadRegistered(@RequestParam("file") MultipartFile file) {

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
            Path path = Paths.get(uploadDir + originalFilename);

            Files.write(path, file.getBytes());

            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 분석 결과 받기
     */
    @PostMapping("/{analyze_id}")
    public ResponseEntity<?> getAnalyzeResult() {
        return null;
    }
}
