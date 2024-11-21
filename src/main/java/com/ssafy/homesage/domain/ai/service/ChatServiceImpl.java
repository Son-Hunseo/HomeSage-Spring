package com.ssafy.homesage.domain.ai.service;

import com.ssafy.homesage.domain.ai.mapper.ChatMapper;
import com.ssafy.homesage.domain.ai.model.dto.*;
import com.ssafy.homesage.domain.ai.model.entity.Message;
import com.ssafy.homesage.domain.ai.model.entity.ChatRoom;
import com.ssafy.homesage.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService{

    private final JwtUtil jwtUtil;
    private final ChatMapper chatMapper;

    private final WebClient webClient = WebClient.builder().build();

    @Value("${server.ip}")
    private String serverIp;

    @Value("${ai.port}")
    private String aiPort;

    @Override
    public ChatListResponseDto getChatList(String accessToken) {

        // 토큰에서 사용자의 이메일 추출
        String userEmail = jwtUtil.getUserEmail(accessToken, "AccessToken");

        // 사용자의 이메일을 통해 채팅방 리스트 조회
        List<ChatRoom> chatRoomList = chatMapper.getChatList(userEmail);

        ChatListResponseDto chatListResponseDto = ChatListResponseDto
                .builder()
                .chatRoomList(chatRoomList)
                .build();

        return chatListResponseDto;
    }

    @Override
    @Transactional // readOnly = false 가 디폴트
    public CreateChatRoomResponseDto createChatRoom(String accessToken, CreateChatRoomRequestDto createChatRoomRequestDto) {

        // 토큰에서 사용자의 이메일 추출
        String userEmail = jwtUtil.getUserEmail(accessToken, "AccessToken");

        // 사용자의 이메일을 통해 새로운 채팅방을 생성
        chatMapper.createChatRoom(userEmail, createChatRoomRequestDto.chatRoomName());

        // 해당 채팅방의 id 가져오기 (하나의 트랜잭션이기 때문에 같은 세션 보장됨)
        int newChatRoomId = chatMapper.getLastInsertedId();

        CreateChatRoomResponseDto createChatRoomResponseDto = CreateChatRoomResponseDto
                .builder()
                .chatRoomId(newChatRoomId)
                .chatRoomName(createChatRoomRequestDto.chatRoomName())
                .build();

        // 디폴트 메시지 넣기
        chatMapper.insertDefaultChatMessage(newChatRoomId);

        return createChatRoomResponseDto;
    }

    @Override
    public boolean checkCanAccessChatRoom(String accessToken, int chatRoomId) {

        // 해당하는 채팅방이 없을 경우
        if (!chatMapper.isExistChatRoom(chatRoomId)) {
            return false;
        }

        // 토큰에서 사용자의 이메일 추출
        String userEmail = jwtUtil.getUserEmail(accessToken, "AccessToken");

        // 현재 접근하고자하는 채팅방을 가지고있는 사용자의 이메일 추출
        String chatRoomOwnerEmail = chatMapper.getChatRoomOwnerEmail(chatRoomId);

        // 같은지 확인
        return userEmail.equals(chatRoomOwnerEmail);
    }

    @Override
    public ChatMessageListResponseDto getChatMessageList(int chatRoomId) {

        // AI 메시지 가져오기
        List<Message> aiMessageList = chatMapper.getAIMessageList(chatRoomId);

        // HUMAN 메시지 가져오기
        List<Message> humanMessageList = chatMapper.getHumanMessageList(chatRoomId);

        return ChatMessageListResponseDto
                .builder()
                .aiMessageList(aiMessageList)
                .humanMessageList(humanMessageList)
                .build();
    }

    @Override
    @Transactional
    public void deleteChatRoom(int chatRoomId) {
        chatMapper.deleteChatMessageList(chatRoomId); // 외래키 걸려서 자식 레코드 먼저 삭제
        chatMapper.deleteChatRoom(chatRoomId);
    }

    @Override
    @Transactional
    public CompletableFuture<AIServerChatResponseDto> getAIResponse(ChatRequestDto chatRequestDto, int chatRoomId) {

        // 최근 5개의 메시지를 history로 만듬
        List<Message> chatHistory = chatMapper.getChatHistory(chatRoomId);

        String userMessage = chatRequestDto.message();
        // 마지막 메시지 + 1이 현재 메시지의 seq
        int userMessageSeq = chatHistory.get(chatHistory.size() - 1).getMessageSeq() + 1;

        // 요청 유저 메시지를 DB에 저장
        chatMapper.insertUserMessage(userMessage, userMessageSeq, chatRoomId);

        AIServerChatRequestDto aiServerChatRequestDto = new AIServerChatRequestDto(chatHistory, userMessage);

        // FastAPI로 요청 보내기
        String url = new StringBuilder()
                .append("http://")
                .append(serverIp)
                .append(":")
                .append(aiPort)
                .append("/chat")
                .toString();

        Mono<AIServerChatResponseDto> aiResponseMono = webClient.post()
                .uri(url)
                .bodyValue(aiServerChatRequestDto)
                .retrieve()
                .bodyToMono(AIServerChatResponseDto.class)
                .map(response -> {
                    String message = response.message();
                    int messageSeq = userMessageSeq + 1;
                    return AIServerChatResponseDto.builder()
                            .message(message)
                            .messageSeq(messageSeq)
                            .build();
                });

        // 응답 받은 AI 메시지를 DB에 저장 (seq = 유저 메시지의 seq + 1)
        aiResponseMono.subscribe(aiResponse -> chatMapper.insertAIMessage(
                aiResponse.message(), userMessageSeq + 1, chatRoomId));

        // 반환
        return aiResponseMono.toFuture();
    }
}
