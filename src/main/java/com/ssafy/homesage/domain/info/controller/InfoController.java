package com.ssafy.homesage.domain.info.controller;

import com.ssafy.homesage.domain.info.model.dto.ResponseInfoListDto;
import com.ssafy.homesage.domain.info.model.dto.ResponseTotalPageNumDto;
import com.ssafy.homesage.domain.info.service.InfoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "부동산 정보 컨트롤러", description = "부동산 정보 관련 컨트롤러")
@RestController
@RequestMapping("/info")
public class InfoController {

    private final InfoService infoService;

    /**
     * 정보 리스트 반환
     */
    @GetMapping("/page/{page_num}")
    public ResponseEntity<?> getTargetPageInfos(@PathVariable("page_num") int pageNum) {

        ResponseInfoListDto responseInfoListDto = infoService.getTargetPageInfos(pageNum);

        if (responseInfoListDto.infoList().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(responseInfoListDto);
    }

    /**
     * 글 조회수 증가
     */
    @GetMapping("/{info_num}")
    public ResponseEntity<?> increaseViews(@PathVariable("info_num") int infoNum) {

        infoService.increaseViews(infoNum);

        // 이후에 없는 info_num 을 요청하면 400에러 리턴하기

        return ResponseEntity.ok().build();
    }

    /**
     * 총 페이지 수 반환
     */
    @GetMapping("/num")
    public ResponseEntity<?> getTotalNumOfPage() {

        ResponseTotalPageNumDto responseTotalPageNumDto = infoService.getTotalNumOfPage();

        return ResponseEntity.ok().body(responseTotalPageNumDto);
    }
}
