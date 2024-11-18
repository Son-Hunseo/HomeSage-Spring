package com.ssafy.homesage.domain.ai.model.dto;

import lombok.Builder;

@Builder
public record AIServerChatResponseDto(String message, int messageSeq) {
}
