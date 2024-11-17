package com.ssafy.homesage.domain.ai.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {

    HUMAN("HUMAN"), AI("AI");

    private final String type;
}
