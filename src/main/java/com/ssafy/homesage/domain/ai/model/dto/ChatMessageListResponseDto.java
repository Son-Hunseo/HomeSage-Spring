package com.ssafy.homesage.domain.ai.model.dto;

import com.ssafy.homesage.domain.ai.model.entity.Message;
import lombok.Builder;
import java.util.List;

@Builder
public record ChatMessageListResponseDto(
        List<Message> humanMessageList,
        List<Message> aiMessageList) {
}
