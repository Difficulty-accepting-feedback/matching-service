package com.grow.matching_service.matching.presentation.controller;

import com.grow.matching_service.matching.application.dto.MatchingResponse;
import com.grow.matching_service.matching.application.service.MatchingService;
import com.grow.matching_service.matching.domain.dto.MatchingUpdateRequest;
import com.grow.matching_service.matching.domain.enums.Category;
import com.grow.matching_service.matching.presentation.dto.MatchingRequest;
import com.grow.matching_service.matching.presentation.dto.rsdata.RsData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/matching")
public class MatchingController {

    private final MatchingService matchingService;

    /**
     * 매칭 정보를 저장합니다.
     *
     * @param request 매칭 요청 DTO
     */
    @PostMapping("/save")
    public RsData<String> createMatching(@Valid @RequestBody MatchingRequest request,
                                         @RequestHeader("X-Authorization-Id") Long memberId) {
        matchingService.createMatching(request, memberId);

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
    public RsData<List<MatchingResponse>> getAllMatching(@RequestHeader("X-Authorization-Id") Long memberId,
                                                         @RequestParam("category") Category category) {
        log.info("[MATCH] 카테고리별 회원 매칭 목록 조회 요청 - category: {}, memberId: {}",
                category, memberId);

        List<MatchingResponse> responses = matchingService.getMatchingsByCategory(category, memberId);

        return new RsData<>(
                "200",
                "회원별 매칭 정보 조회 완료",
                responses
        );
    }


    /**
     * 매칭 정보를 수정합니다.
     *
     * @param matchingId 수정할 매칭 ID
     * @param request    매칭 수정 요청 DTO
     * @return 수정 결과
     */
    @PatchMapping("/update/{matchingId}")
    public RsData<String> updateMatching(@PathVariable Long matchingId,
                                         @Valid @RequestBody MatchingUpdateRequest request) {
        log.info("[MATCH UPDATE] 매칭 정보 수정 요청 - matchingId: {}, request: {}", matchingId, request);

        matchingService.updateMatching(matchingId, request);

        log.info("[MATCH UPDATE] 매칭 정보 수정 완료 - matchingId: {}", matchingId);

        return new RsData<>(
                "200",
                "매칭 정보 수정 완료"
        );
    }

    /**
     * 매칭 정보를 삭제합니다.
     * @param matchingId 삭제할 매칭 ID
     * @return 삭제 결과 - 성공: 200, 실패: 예외 발생
     */
    @DeleteMapping("/delete/{matchingId}")
    public RsData<String> deleteMatching(@PathVariable Long matchingId,
                                         @RequestHeader("X-Authorization-Id") Long memberId) {
        log.info("[MATCH DELETE] 매칭 정보 삭제 요청 - matchingId: {}", matchingId);

        matchingService.deleteMatching(matchingId, memberId);

        log.info("[MATCH DELETE] 매칭 정보 삭제 완료 - matchingId: {}", matchingId);

        return new RsData<>(
                "200",
                "매칭 정보 삭제 완료"
        );
    }
}
