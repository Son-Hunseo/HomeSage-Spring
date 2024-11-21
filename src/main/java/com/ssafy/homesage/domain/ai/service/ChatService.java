package com.ssafy.homesage.domain.ai.service;

import com.ssafy.homesage.domain.ai.model.dto.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.concurrent.CompletableFuture;

public interface ChatService {

    public ChatListResponseDto getChatList(String accessToken);

    public CreateChatRoomResponseDto createChatRoom(String accessToken, CreateChatRoomRequestDto createChatRoomRequestDto);

    public boolean checkCanAccessChatRoom(String accessToken, int chatRoomId);

    public ChatMessageListResponseDto getChatMessageList(int chatRoomId);

    public void deleteChatRoom(int chatRoomId);

    public CompletableFuture<AIServerChatResponseDto> getAIResponse(ChatRequestDto chatRequestDto, int chatRoomId);
}
