package com.ssafy.homesage.domain.ai.model.dto;

import com.ssafy.homesage.domain.ai.model.entity.Analyze;
import lombok.Builder;
import java.util.List;

@Builder
public record AnalyzeListResponseDto(List<Analyze> analyzeList) {
}
