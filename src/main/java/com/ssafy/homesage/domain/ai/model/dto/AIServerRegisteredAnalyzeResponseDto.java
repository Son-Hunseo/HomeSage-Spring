package com.ssafy.homesage.domain.ai.model.dto;

import lombok.Builder;

@Builder
public record AIServerRegisteredAnalyzeResponseDto(String result, String summary, String score) {
}
