package com.ssafy.homesage.domain.ai.model.dto;

import lombok.Builder;

@Builder
public record CreateAnalyzeResponseDto(int analyzeId, String analyzeName) {
}
