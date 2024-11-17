package com.ssafy.homesage.domain.ai.model.dto;

import com.ssafy.homesage.domain.ai.model.entity.AIMessage;
import com.ssafy.homesage.domain.ai.model.entity.HumanMessage;
import lombok.Builder;
import java.util.List;

@Builder
public record ChatMessageListResponseDto(
        List<HumanMessage> humanMessageList,
        List<AIMessage> aiMessageList) {
}
