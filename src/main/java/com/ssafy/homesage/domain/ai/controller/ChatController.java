package com.ssafy.homesage.domain.ai.controller;

import com.ssafy.homesage.domain.ai.model.dto.*;
import com.ssafy.homesage.domain.ai.model.entity.Message;
import com.ssafy.homesage.domain.ai.model.enums.MessageType;
import com.ssafy.homesage.domain.ai.service.ChatService;
import com.ssafy.homesage.global.util.HeaderUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.ssafy.homesage.domain.ai.model.enums.MessageType.AI;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "챗봇 관련 컨트롤러", description = "챗봇 응답 받기")
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    /**
     * 해당 유저의 채팅방 리스트 반환
     */
    @GetMapping
    public ResponseEntity<?> getChatList(HttpServletRequest request) {

        // Http Header 의 Authorization (Access Token) 추출
        String accessToken = HeaderUtil.getAccessToken(request);

        // 해당 유저의 email로 해당하는 채팅방 리스트 반환
        ChatListResponseDto chatListResponseDto = chatService.getChatList(accessToken);

        return ResponseEntity.ok(chatListResponseDto);
    }

    /**
     * 새로운 채팅방 만들기
     */
    @PostMapping
    public ResponseEntity<?> createChatRoom(HttpServletRequest request) {

        // Http Header 의 Authorization (Access Token) 추출
        String accessToken = HeaderUtil.getAccessToken(request);

        // 해당 유저의 email로 새로운 채팅방을 생성하고, 생성한 채팅방의 id 반환
        CreateChatRoomResponseDto createChatRoomResponseDto = chatService.createChatRoom(accessToken);

        return ResponseEntity.ok(createChatRoomResponseDto);
    }

    /**
     * 채팅방 입장
     * - 해당 chat_room을 생성하지 않은 유저는 접근 불가
     */
    @GetMapping("/{chat_room_id}")
    public ResponseEntity<?> getChatMessageList(
            @PathVariable("chat_room_id") int chatRoomId,
            HttpServletRequest request) {

        // Http Header 의 Authorization (Access Token) 추출
        String accessToken = HeaderUtil.getAccessToken(request);

        // email로 해당 유저가 이 채팅방에 접근이 가능한지 여부를 반환
        boolean canAccess = chatService.checkCanAccessChatRoom(accessToken, chatRoomId);

        if (canAccess) {
            // 정상적인 채팅방 접속 로직
            ChatMessageListResponseDto chatMessageResponseDto = chatService.getChatMessageList(chatRoomId);

            return ResponseEntity.ok(chatMessageResponseDto);
        }

        // 권한 없음 오류 반환
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * 채팅방 삭제
     */
    @DeleteMapping("/{chat_room_id}")
    public ResponseEntity<?> deleteChatRoom(
            @PathVariable("chat_room_id") int chatRoomId,
            HttpServletRequest request) {

        // Http Header 의 Authorization (Access Token) 추출
        String accessToken = HeaderUtil.getAccessToken(request);

        // email로 해당 유저가 이 채팅방에 접근이 가능한지 여부를 반환
        boolean canAccess = chatService.checkCanAccessChatRoom(accessToken, chatRoomId);

        if (canAccess) {
            // 정상적인 채팅방 삭제 로직
            chatService.deleteChatRoom(chatRoomId);

            return ResponseEntity.ok().build();
        }

        // 권한 없음 오류 반환
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * AI 챗봇 응답 받기
     */
    @PostMapping("/{chat_room_id}")
    public ResponseEntity<?> getAIResponse(
            @PathVariable("chat_room_id") int chatRoomId,
            HttpServletRequest request,
            @RequestBody ChatRequestDto chatRequestDto) throws ExecutionException, InterruptedException {

        // Http Header 의 Authorization (Access Token) 추출
        String accessToken = HeaderUtil.getAccessToken(request);

        // email로 해당 유저가 이 채팅방에 접근이 가능한지 여부를 반환
        boolean canAccess = chatService.checkCanAccessChatRoom(accessToken, chatRoomId);

        if (canAccess) {
            // 정상적인 AI 챗봇 응답 요청 로직
            CompletableFuture<AIServerChatResponseDto> aiResponse = chatService.getAIResponse(chatRequestDto, chatRoomId);

            CompletableFuture<ChatResponseDto> chatResponseDto = aiResponse.thenApply(response -> {
                Message aiMessage = Message.builder()
                        .type(AI)
                        .message(response.message())
                        .messageSeq(response.messageSeq())
                        .build();
                return new ChatResponseDto(aiMessage.getType(), aiMessage.getMessage(), aiMessage.getMessageSeq());
            });

            return ResponseEntity.ok().body(chatResponseDto.get());
        }

        // 권한 없음 오류 반환
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

}
