package com.ssafy.homesage.domain.ai.model.dto;

import com.ssafy.homesage.domain.ai.model.entity.ChatRoom;
import lombok.Builder;
import java.util.List;

@Builder
public record ChatListResponseDto(List<ChatRoom> chatRoomList) {

}
