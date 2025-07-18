package com.grow.matching_service.matching.application.service;

import com.grow.matching_service.matching.application.dto.MatchingResponse;
import com.grow.matching_service.matching.domain.enums.Category;
import com.grow.matching_service.matching.presentation.dto.MatchingRequest;
import com.grow.matching_service.matching.domain.model.Matching;
import com.grow.matching_service.matching.domain.repository.MatchingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingServiceImpl implements MatchingService {

    private final MatchingRepository matchingRepository;

    /**
     * 매칭 정보를 저장하고, DB에 저장 -> 이벤트를 발행합니다.
     *
     * @param request 매칭 요청 DTO
     */
    @Override
    @Transactional
    public void createMatching(MatchingRequest request) {
        // 입력값 기본 검증 (나머지 필드에 대한 검증은 DTO 객체에서 실행)
        if (request == null) {
            throw new IllegalArgumentException("매칭 요청이 null 입니다.");
        }

        // 도메인 생성
        Matching matching = createNewDomain(request);

        // 레포지토리에 저장
        matchingRepository.save(matching);
    }

    /**
     * 카테고리와 회원 ID를 기준으로 매칭 정보를 조회합니다.
     * @param category 조회할 카테고리
     * @param memberId 조회할 회원 ID
     * @return 매칭 정보 DTO 리스트
     */
    @Override
    @Transactional(readOnly = true) // 조회용 -> 읽기 전용으로 설정
    public List<MatchingResponse> getMatchingsByCategory(Category category,
                                                         Long memberId) {
        // 서비스 로직: 기본 null 체크만 (입력 필터링)
        validateCategoryAndMemberId(category, memberId);

        // 레포지토리에서 데이터를 조회 (최신 저장순 정렬) -> 도메인을 DTO 객체로 변환
        List<MatchingResponse> responses = matchingRepository.findByCategoryAndMemberId(category, memberId)
                .stream()
                .map(MatchingResponse::from)
                .toList();

        // 단순 로깅 메서드
        logging(category, memberId, responses);

        return responses;
    }

    private void logging(Category category, Long memberId, List<MatchingResponse> responses) {
        if (responses.isEmpty()) {
            log.info("[MATCH] 해당 카테고리에 매칭 정보가 없습니다. - category: {}, memberId: {}", category, memberId);
        } else {
            log.info("[MATCH] 카테고리별 회원 매칭 조회 완료 - category: {}, memberId: {}, 조회된 건수: {}", category, memberId, responses.size());
        }
    }

    private void validateCategoryAndMemberId(Category category, Long memberId) {
        if (category == null) {
            throw new IllegalArgumentException("카테고리는 null일 수 없습니다.");
        }

        if (memberId == null || memberId <= 0L) {
            throw new IllegalArgumentException("유효하지 않은 회원 ID 입니다.");
        }
    }

    private Matching createNewDomain(MatchingRequest request) {
        return Matching.createNew(
                request.getMemberId(),
                request.getCategory(),
                request.getMostActiveTime(),
                request.getLevel(),
                request.getAge(),
                request.getIsAttending(),
                request.getIntroduction()
        );
    }
}
