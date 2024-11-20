package com.ssafy.homesage.domain.ai.model.dto;

import lombok.Builder;

// FastAPI 서버로 보내기위해 snake_case
@Builder
public record AIServerAnalyzeRequestDto(String registered_text, String ledger_text) {
}
