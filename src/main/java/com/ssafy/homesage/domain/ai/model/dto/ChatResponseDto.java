package com.ssafy.homesage.domain.ai.model.dto;

import com.ssafy.homesage.domain.ai.model.enums.MessageType;
import lombok.Builder;

@Builder
public record ChatResponseDto(MessageType type, String message, int messageSeq) {
}
