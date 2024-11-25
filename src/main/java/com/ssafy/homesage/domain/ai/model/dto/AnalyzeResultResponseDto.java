package com.ssafy.homesage.domain.ai.model.dto;

import lombok.Builder;

@Builder
public record AnalyzeResultResponseDto(String result, String summary, String score) {
}
