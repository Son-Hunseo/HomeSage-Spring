package com.ssafy.homesage.domain.info.service;

import com.ssafy.homesage.domain.info.model.dto.ResponseInfoListDto;
import com.ssafy.homesage.domain.info.model.dto.ResponseTotalPageNumDto;

public interface InfoService {

    ResponseInfoListDto getTargetPageInfos(int pageNum);

    void increaseViews(int infoNum);

    ResponseTotalPageNumDto getTotalNumOfPage();
}
