package com.ssafy.homesage.domain.ai.service;

import com.ssafy.homesage.domain.ai.mapper.AnalyzeMapper;
import com.ssafy.homesage.domain.ai.model.dto.*;
import com.ssafy.homesage.domain.ai.model.entity.Analyze;
import com.ssafy.homesage.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyzeServiceImpl implements AnalyzeService {

    @Value("${server.ip}")
    private String serverIp;

    @Value("${static.port}")
    private String staticPort;

    @Value("${ai.port}")
    private String aiPort;

    @Value("${ocr.url}")
    private String ocrUrl;

    @Value("${x.ocr.secret}")
    private String xOcrSecret;

    private final JwtUtil jwtUtil;
    private final AnalyzeMapper analyzeMapper;
    private static final RestTemplate restTemplate = new RestTemplate();
    private final WebClient webClient = WebClient.builder().build();

    @Override
    public AnalyzeListResponseDto getAnalyzeList(String accessToken) {

        // 토큰에서 사용자의 이메일 추출
        String userEmail = jwtUtil.getUserEmail(accessToken, "AccessToken");

        // 사용자의 이메일을 통해 분석방 리스트 조회
        List<Analyze> analyzeRoomList = analyzeMapper.getAnalyzeList(userEmail);

        AnalyzeListResponseDto analyzeListResponseDto = AnalyzeListResponseDto
                .builder()
                .analyzeList(analyzeRoomList)
                .build();

        return analyzeListResponseDto;
    }

    @Override
    @Transactional
    public CreateAnalyzeResponseDto createAnalyze(String accessToken) {

        // 토큰에서 사용자의 이메일 추출
        String userEmail = jwtUtil.getUserEmail(accessToken, "AccessToken");

        // 사용자의 이메일을 통해 새로운 분석 방을 생성
        analyzeMapper.createAnalyze(userEmail);

        // 해당 분석 방의 id 가져오기 (하나의 트랜잭션이기 때문에 같은 세션 보장됨)
        int newAnalyzeId = analyzeMapper.getLastInsertedId();

        CreateAnalyzeResponseDto createAnalyzeResponseDto = CreateAnalyzeResponseDto
                .builder()
                .analyzeId(newAnalyzeId)
                .build();

        return createAnalyzeResponseDto;
    }

    @Override
    public boolean checkCanAccessChatRoom(String accessToken, int analyzeId) {

        // 해당하는 분석방이 없을 경우
        if (!analyzeMapper.isExistAnalyze(analyzeId)) {
            return false;
        }

        // 토큰에서 사용자의 이메일 추출
        String userEmail = jwtUtil.getUserEmail(accessToken, "AccessToken");

        // 현재 접근하고자하는 분석방을 가지고있는 사용자의 이메일 추출
        String analyzeOwnerEmail = analyzeMapper.getAnalyzeOwnerEmail(analyzeId);

        // 같은지 확인
        return userEmail.equals(analyzeOwnerEmail);
    }

    @Override
    public AnalyzeInfoResponseDto getAnalyzeInfo(int analyzeId) {

        // Analyze 가져오기
        AnalyzeInfoResponseDto analyzeInfoResponseDto = analyzeMapper.getAnalyzeInfo(analyzeId);

        return analyzeInfoResponseDto;
    }

    @Override
    @Transactional
    public void deleteAnalyze(int analyzeId) {
        analyzeMapper.deleteAnalyze(analyzeId);
    }

    @Override
    @Transactional
    public void saveRegisteredUrl(int analyzedId, String fileName) {

        String url = new StringBuilder()
                        .append("http://")
                        .append(serverIp)
                        .append(":")
                        .append(staticPort)
                        .append("/static/registered/")
                        .append(fileName)
                        .toString();

        analyzeMapper.saveRegisteredUrl(analyzedId, url);
    }

    @Override
    @Transactional
    public void saveLedgerUrl(int analyzedId, String fileName) {
        String url = new StringBuilder()
                .append("http://")
                .append(serverIp)
                .append(":")
                .append(staticPort)
                .append("/static/ledger/")
                .append(fileName)
                .toString();

        analyzeMapper.saveLedgerUrl(analyzedId, url);
    }

    @Override
    @Transactional
    public CompletableFuture<AnalyzeResultResponseDto> getAnalyzeResult(int analyzedId) {

        String baseOcrUrl = ocrUrl;

        // 등기부 등본 url 가져오기
        String registeredUrl = analyzeMapper.getRegisteredUrl(analyzedId);

        // 건축물 대장 url 가져오기
        String ledgerUrl = analyzeMapper.getLedgerUrl(analyzedId);

        // 등기부 등본 ocr 결과 (파싱 이후)
        String registeredOcrResult = getOcrResult(baseOcrUrl, registeredUrl, xOcrSecret);

        // 건축물 대장 ocr 결과 (파싱 이후)
        String ledgerOcrResult = getOcrResult(baseOcrUrl, ledgerUrl, xOcrSecret);

        // 분석 결과 요청
        AIServerAnalyzeRequestDto aiServerAnalyzeRequestDto = AIServerAnalyzeRequestDto
                .builder()
                .registered_text(registeredOcrResult)
                .ledger_text(ledgerOcrResult)
                .build();

        // FastAPI로 요청 보내기
        String url = new StringBuilder()
                .append("http://")
                .append(serverIp)
                .append(":")
                .append(aiPort)
                .append("/analyze")
                .toString();

        Mono<AnalyzeResultResponseDto> aiResponseMono = webClient.post()
                .uri(url)
                .bodyValue(aiServerAnalyzeRequestDto)
                .retrieve()
                .bodyToMono(AIServerAnalyzeResponseDto.class)
                .map(response -> {
                    String result = response.result();
                    return AnalyzeResultResponseDto
                            .builder()
                            .result(result)
                            .build();
                });

        // 응답 받은 분석 결과를 DB에 저장
        aiResponseMono.subscribe(aiResponse -> analyzeMapper.insertAnalyzeResult(
                aiResponse.result(), analyzedId));

        return aiResponseMono.toFuture();
    }

    private static String getOcrResult(String ocrUrl, String imgUrl, String xOcrSecret) {

        String result;

        // 업로드한 이미지가 없으면 빈 결과 리턴
        if (imgUrl.isEmpty()) {
            return "";
        }

        String jsonDataTemplate = """
        {   
            "images": [
                {
                    "format": "png",
                    "name": "medium",
                    "data": null,
                    "url": "%s"
                }
            ],
            "lang": "ko",
            "requestId": "string",
            "resultType": "string",
            "timestamp": 1234567890123,
            "version": "V1"
        }
        """;

        String jsonData = String.format(jsonDataTemplate, imgUrl);

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-OCR-SECRET", xOcrSecret);

        // HttpEntity에 JSON 데이터와 헤더를 설정
        HttpEntity<String> request = new HttpEntity<>(jsonData, headers);

        // 응답 받기
        String ocrResponse = restTemplate.postForEntity(ocrUrl, request, String.class).getBody();

        if (ocrResponse != null) {
            // JSON 파싱
            JSONObject jsonObject = new JSONObject(ocrResponse);
            JSONArray fields = jsonObject.getJSONArray("images").getJSONObject(0).getJSONArray("fields");

            result = parseOcrResult(fields);
        } else {
            log.info("OCR 응답 결과 없음");
            result = "";
        }

        return result;
    }

    private static String parseOcrResult(JSONArray fields) {
        StringBuilder result = new StringBuilder();
        double lastY = -1;

        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);
            String inferText = field.getString("inferText");
            double currentY = field.getJSONObject("boundingPoly").getJSONArray("vertices").getJSONObject(3).getDouble("y");

            if (i > 0 && Math.abs(currentY - lastY) >= 10) {
                result.append("\n"); // 줄바꿈 문자 추가
            }

            result.append(inferText);

            // 구분자 추가
            if (i < fields.length() - 1) { // 마지막 요소가 아닐 때만 구분자 추가
                result.append(" ");
            }

            lastY = currentY;
        }
        return result.toString();
    }
}
