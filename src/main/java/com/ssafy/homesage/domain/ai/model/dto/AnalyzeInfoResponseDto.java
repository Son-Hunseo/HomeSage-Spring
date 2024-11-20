package com.ssafy.homesage.domain.ai.model.dto;

import lombok.Builder;

@Builder
public record AnalyzeInfoResponseDto(String registeredUrl, String ledgerUrl, String registeredResult, String ledgerResult) {
}
