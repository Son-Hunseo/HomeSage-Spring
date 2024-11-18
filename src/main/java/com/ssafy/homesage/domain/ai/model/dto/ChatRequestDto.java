package com.ssafy.homesage.domain.ai.model.dto;

import com.ssafy.homesage.domain.ai.model.enums.MessageType;
import lombok.Builder;

@Builder
public record ChatRequestDto(MessageType type, String message) {
}
