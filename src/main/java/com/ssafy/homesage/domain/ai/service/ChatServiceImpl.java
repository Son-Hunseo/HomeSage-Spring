package com.ssafy.homesage.domain.ai.service;

import com.ssafy.homesage.domain.ai.mapper.ChatMapper;
import com.ssafy.homesage.domain.ai.model.dto.ChatListResponseDto;
import com.ssafy.homesage.domain.ai.model.dto.ChatMessageListResponseDto;
import com.ssafy.homesage.domain.ai.model.dto.CreateChatRoomResponseDto;
import com.ssafy.homesage.domain.ai.model.entity.AIMessage;
import com.ssafy.homesage.domain.ai.model.entity.ChatRoom;
import com.ssafy.homesage.domain.ai.model.entity.HumanMessage;
import com.ssafy.homesage.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService{

    private final JwtUtil jwtUtil;
    private final ChatMapper chatMapper;

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
    public CreateChatRoomResponseDto createChatRoom(String accessToken) {

        // 토큰에서 사용자의 이메일 추출
        String userEmail = jwtUtil.getUserEmail(accessToken, "AccessToken");

        // 사용자의 이메일을 통해 새로운 채팅방을 생성
        chatMapper.createChatRoom(userEmail);

        // 해당 채팅방의 id 가져오기 (하나의 트랜잭션이기 때문에 같은 세션 보장됨)
        int newChatRoomId = chatMapper.getLastInsertedId();

        CreateChatRoomResponseDto createChatRoomResponseDto = CreateChatRoomResponseDto
                .builder()
                .chatRoomId(newChatRoomId)
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
        List<AIMessage> aiMessageList = chatMapper.getAIMessageList(chatRoomId);

        // HUMAN 메시지 가져오기
        List<HumanMessage> humanMessageList = chatMapper.getHumanMessageList(chatRoomId);

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
}
