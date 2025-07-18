package com.grow.matching_service.matching.presentation.controller;

import com.grow.matching_service.matching.application.dto.MatchingResponse;
import com.grow.matching_service.matching.application.service.MatchingService;
import com.grow.matching_service.matching.domain.enums.Category;
import com.grow.matching_service.matching.presentation.dto.MatchingRequest;
import com.grow.matching_service.matching.presentation.dto.rsdata.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     */
    @PostMapping("/save")
    public RsData<String> createMatching(@Valid @RequestBody MatchingRequest request) {
        matchingService.createMatching(request);

        return new RsData<>(
                "201",
                "매칭 정보 생성 완료"
        );
    }

    /**
     * 카테고리를 기준으로 특정 회원의 매칭 정보를 전체 조회합니다.
     *
     * @param category 조회할 카테고리
     * @param memberId 조회할 회원 ID
     * @return 매칭 정보 DTO 리스트
     */
    @GetMapping("/check")
    public RsData<List<MatchingResponse>> getAllMatching(@RequestParam("category") Category category,
                                                         @RequestParam("memberId") Long memberId) {
        log.info("[MATCH] 카테고리별 회원 매칭 목록 조회 요청 - category: {}, memberId: {}",
                category, memberId);

        List<MatchingResponse> responses = matchingService.getMatchingsByCategory(category, memberId);

        return new RsData<>(
                "200",
                "회원별 매칭 정보 조회 완료",
                responses
        );
    }
}
