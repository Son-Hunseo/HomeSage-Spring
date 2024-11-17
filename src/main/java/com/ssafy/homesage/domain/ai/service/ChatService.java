package com.ssafy.homesage.domain.ai.service;

import com.ssafy.homesage.domain.ai.model.dto.ChatListResponseDto;
import com.ssafy.homesage.domain.ai.model.dto.ChatMessageListResponseDto;
import com.ssafy.homesage.domain.ai.model.dto.CreateChatRoomResponseDto;
import jakarta.servlet.http.HttpServletRequest;

public interface ChatService {

    public ChatListResponseDto getChatList(String accessToken);

    public CreateChatRoomResponseDto createChatRoom(String accessToken);

    public boolean checkCanAccessChatRoom(String accessToken, int chatRoomId);

    public ChatMessageListResponseDto getChatMessageList(int chatRoomId);

    public void deleteChatRoom(int chatRoomId);
}
