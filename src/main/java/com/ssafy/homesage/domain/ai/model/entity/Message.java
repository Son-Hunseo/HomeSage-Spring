package com.ssafy.homesage.domain.ai.model.entity;

import com.ssafy.homesage.domain.ai.model.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Message {
    private MessageType type;
    private String message;
    private int messageSeq;
}
