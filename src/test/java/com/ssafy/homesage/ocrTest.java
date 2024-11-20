package com.ssafy.homesage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class ocrTest {
    private static final RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) throws JSONException {
        String url = "https://rabm5binpg.apigw.ntruss.com/custom/v1/35511/69f269afbfa7e7483736370073e2b8f876b48a1e1f439f7d00874133378e87e6/general";

        String jsonData = """
        {
            "images": [
                {
                    "format": "png",
                    "name": "medium",
                    "data": null,
                    "url": "http://121.88.240.38:808/static/img/register.png"
                }
            ],
            "lang": "ko",
            "requestId": "string",
            "resultType": "string",
            "timestamp": 1234567890123,
            "version": "V1"
        }
        """;

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-OCR-SECRET", "QktpcndrbEtnb0RpR29VY2hKRURWd0RmR3FaQUFLc1U=");

        // HttpEntity에 JSON 데이터와 헤더를 설정
        HttpEntity<String> request = new HttpEntity<>(jsonData, headers);

        // 응답 받기
        String response = restTemplate.postForEntity(url, request, String.class).getBody();

        if (response != null) {
            // JSON 파싱
            JSONObject jsonObject = new JSONObject(response);
            JSONArray fields = jsonObject.getJSONArray("images").getJSONObject(0).getJSONArray("fields");

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

            System.out.println(result.toString());
        } else {
            System.out.println("응답이 없습니다.");
        }
    }
}