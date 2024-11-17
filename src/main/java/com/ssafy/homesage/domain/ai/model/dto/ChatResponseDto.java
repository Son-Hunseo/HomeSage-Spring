package com.ssafy.homesage.domain.ai.model.dto;

import com.ssafy.homesage.domain.ai.model.entity.AIMessage;
import lombok.Builder;

@Builder
public record ChatResponseDto(AIMessage aiMessage) {
}
