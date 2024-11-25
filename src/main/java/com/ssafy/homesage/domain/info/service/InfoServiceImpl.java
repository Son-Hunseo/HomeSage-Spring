package com.ssafy.homesage.domain.info.service;

import com.ssafy.homesage.domain.info.mapper.InfoMapper;
import com.ssafy.homesage.domain.info.model.dto.ResponseInfoListDto;
import com.ssafy.homesage.domain.info.model.dto.ResponseTotalPageNumDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InfoServiceImpl implements InfoService {

    private final InfoMapper infoMapper;
    private final int infoPerPage = 7;

    @Override
    public ResponseInfoListDto getTargetPageInfos(int pageNum) {

        // 일단 이렇게 처리하고 나중에 예외처리
        if (pageNum == 0) {
            return ResponseInfoListDto
                    .builder().build();
        }

        int offset = infoPerPage * (pageNum - 1);

        return ResponseInfoListDto
                .builder()
                .infoList(infoMapper.getTargetPageInfos(offset))
                .build();
    }

    @Override
    @Transactional
    public void increaseViews(int infoNum) {
        infoMapper.increaseViews(infoNum);
    }

    @Override
    public ResponseTotalPageNumDto getTotalNumOfPage() {
        return ResponseTotalPageNumDto
                .builder()
                .numOfPage(infoMapper.getTotalNumOfPage())
                .build();
    }
}
