package com.grow.matching_service.matching.persistence.controller;

import com.grow.matching_service.matching.application.service.MatchingService;
import com.grow.matching_service.matching.persistence.dto.MatchingRequest;
import com.grow.matching_service.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/matching")
public class MatchingController {

    private final MatchingService matchingService;

    /**
     * 매칭 정보를 저장합니다.
     *
     * @param request 매칭 요청 DTO
     * @return 저장된 매칭 정보 DTO
     */
    @PostMapping("/save")
    public RsData<String> createMatching(@RequestBody MatchingRequest request) {
        matchingService.createMatching(request);

        return new RsData<>(
                "201",
                "매칭 정보 생성 완료"
        );
    }
}
