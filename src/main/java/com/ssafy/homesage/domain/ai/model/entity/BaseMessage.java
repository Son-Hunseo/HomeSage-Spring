package com.ssafy.homesage.domain.ai.model.entity;

import com.ssafy.homesage.domain.ai.model.enums.MessageType;

public abstract class BaseMessage {

    private MessageType type;
    private String message;
    private int messageSeq;
}
