package com.ssafy.homesage.domain.ai.model.dto;

import com.ssafy.homesage.domain.ai.model.entity.Message;
import lombok.Builder;

import java.util.List;

// 해당 서버의 받는 타입과 일치시켜주기 위해서 snake_case 사용했다.
@Builder
public record AIServerChatRequestDto(List<Message> chat_history, String message) {
}
