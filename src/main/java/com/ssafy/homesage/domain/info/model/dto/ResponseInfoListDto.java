package com.ssafy.homesage.domain.info.model.dto;

import com.ssafy.homesage.domain.info.model.entity.Info;
import lombok.Builder;

import java.util.List;

@Builder
public record ResponseInfoListDto(List<Info> infoList) {
}
