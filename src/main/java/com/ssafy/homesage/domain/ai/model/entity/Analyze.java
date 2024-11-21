package com.ssafy.homesage.domain.ai.model.entity;

import com.ssafy.homesage.global.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Analyze extends BaseEntity {
    private int analyzeId;
}