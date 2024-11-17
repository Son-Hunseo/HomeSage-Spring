package com.ssafy.homesage.domain.ai.model.dto;

import com.ssafy.homesage.domain.ai.model.entity.HumanMessage;
import lombok.Builder;

@Builder
public record ChatRequestDto(HumanMessage humanMessage) {
}
