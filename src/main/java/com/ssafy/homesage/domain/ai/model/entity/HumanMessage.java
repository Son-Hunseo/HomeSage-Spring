package com.ssafy.homesage.domain.ai.model.entity;

import com.ssafy.homesage.domain.ai.model.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class HumanMessage extends BaseMessage {

    private MessageType type = MessageType.HUMAN;
    private String message;
    private int messageSeq;
}
