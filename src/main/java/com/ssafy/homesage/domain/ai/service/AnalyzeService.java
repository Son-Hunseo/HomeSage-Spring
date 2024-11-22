package com.ssafy.homesage.domain.ai.service;

import com.ssafy.homesage.domain.ai.model.dto.*;

import java.util.concurrent.CompletableFuture;

public interface AnalyzeService {

    public AnalyzeListResponseDto getAnalyzeList(String accessToken);

    public CreateAnalyzeResponseDto createAnalyze(String accessToken, CreateAnalyzeRequestDto createAnalyzeRequestDto);

    public boolean checkCanAccessChatRoom(String accessToken, int analyzeId);

    public AnalyzeInfoResponseDto getAnalyzeInfo(int analyzeId);

    public void deleteAnalyze(int analyzeId);

    public void saveRegisteredUrl(int analyzedId, String fileName);

    public void saveLedgerUrl(int analyzedId, String fileName);

    public CompletableFuture<AnalyzeResultResponseDto> getRegisteredAnalyzeResult(int analyzedId);

    public CompletableFuture<AnalyzeResultResponseDto> getLedgerAnalyzeResult(int analyzedId);
}
