package com.ssafy.homesage.domain.ai.service;

import com.ssafy.homesage.domain.ai.model.dto.AnalyzeInfoResponseDto;
import com.ssafy.homesage.domain.ai.model.dto.AnalyzeListResponseDto;
import com.ssafy.homesage.domain.ai.model.dto.AnalyzeResultResponseDto;
import com.ssafy.homesage.domain.ai.model.dto.CreateAnalyzeResponseDto;

import java.util.concurrent.CompletableFuture;

public interface AnalyzeService {

    public AnalyzeListResponseDto getAnalyzeList(String accessToken);

    public CreateAnalyzeResponseDto createAnalyze(String accessToken);

    public boolean checkCanAccessChatRoom(String accessToken, int analyzeId);

    public AnalyzeInfoResponseDto getAnalyzeInfo(int analyzeId);

    public void deleteAnalyze(int analyzeId);

    public void saveRegisteredUrl(int analyzedId, String fileName);

    public void saveLedgerUrl(int analyzedId, String fileName);

    public CompletableFuture<AnalyzeResultResponseDto> getRegisteredAnalyzeResult(int analyzedId);

    public CompletableFuture<AnalyzeResultResponseDto> getLedgerAnalyzeResult(int analyzedId);
}
