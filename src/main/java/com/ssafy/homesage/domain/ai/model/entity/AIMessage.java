package com.ssafy.homesage.domain.ai.model.entity;

import com.ssafy.homesage.domain.ai.model.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AIMessage extends BaseMessage {

    private MessageType type = MessageType.AI;
    private String message;
    private int messageSeq;
}
