package com.grow.matching_service.matching.application.service;

import com.grow.matching_service.matching.application.dto.MatchingResponse;
import com.grow.matching_service.matching.domain.dto.MatchingUpdateRequest;
import com.grow.matching_service.matching.domain.enums.Category;
import com.grow.matching_service.matching.application.exception.MatchingNotFoundException;
import com.grow.matching_service.matching.presentation.dto.MatchingRequest;
import com.grow.matching_service.matching.domain.model.Matching;
import com.grow.matching_service.matching.domain.repository.MatchingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.grow.matching_service.matching.application.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingServiceImpl implements MatchingService {

    private final MatchingRepository matchingRepository;

    /**
     * 사용자의 매칭 요청을 처리하여 새로운 매칭을 생성하고 저장합니다.
     * <p>
     * 이 메서드는 주어진 카테고리와 회원 ID를 기반으로 기존 매칭 정보를 조회한 후,
     * 새로운 Matching 도메인을 생성하고 리포지토리에 저장합니다.
     * 카테고리 별 3개를 초과한 매칭이 저장되는 경우 오류가 발생합니다.
     *
     * @param request  매칭 생성에 필요한 요청 데이터 (카테고리 등 포함)
     * @param memberId 매칭을 생성할 회원의 ID
     */
    @Override
    @Transactional
    public void createMatching(MatchingRequest request, Long memberId) {
        // 카테고리별 매칭 정보 조회 (갯수 확인을 위함)
        List<Matching> matchings = matchingRepository.findByCategoryAndMemberId(
                request.getCategory(),
                memberId
        );
        Matching matching = createNewDomain(request, matchings); // 도메인 생성

        // 레포지토리에 저장
        matchingRepository.save(matching);
    }

    /**
     * 카테고리와 회원 ID를 기준으로 매칭 정보를 조회합니다.
     *
     * @param category 조회할 카테고리
     * @param memberId 조회할 회원 ID
     * @return 매칭 정보 DTO 리스트
     */
    @Override
    @Transactional(readOnly = true) // 조회용 -> 읽기 전용으로 설정
    public List<MatchingResponse> getMatchingsByCategory(Category category,
                                                         Long memberId) {
        // 레포지토리에서 데이터를 조회 (최신 저장순 정렬) -> 도메인을 DTO 객체로 변환
        List<MatchingResponse> responses = matchingRepository.findByCategoryAndMemberId(category, memberId)
                .stream()
                .map(MatchingResponse::from)
                .toList();

        // 단순 로깅 메서드
        logging(category, memberId, responses);

        return responses;
    }

    /**
     * 매칭 정보를 수정합니다.
     *
     * @param matchingId 수정할 매칭 ID
     * @param request    매칭 수정 요청 DTO
     */
    @Override
    @Transactional
    public void updateMatching(Long matchingId,
                               MatchingUpdateRequest request) {

        // 도메인 객체 로드 (NotFound 예외)
        Matching matching = matchingRepository.findByMatchingId(matchingId)
                .orElseThrow(() -> new MatchingNotFoundException(MATCHING_NOT_FOUND));

        // request 값으로 도메인 업데이트
        updateMatchingFields(request, matching);

        // 저장
        matchingRepository.save(matching);
    }

    /**
     * 주어진 매칭 ID와 멤버 ID를 사용하여 매칭을 삭제합니다.
     * 이 메서드는 소프트 삭제(soft delete)를 수행하며, 실제로 데이터베이스에서 레코드를 제거하지 않고 상태를 변경하여 삭제된 것으로 표시합니다.
     * 트랜잭션 내에서 실행되며, 매개변수의 유효성을 검사한 후 매칭 객체를 조회하고 삭제 로직을 적용합니다.
     *
     * @param matchingId 삭제할 매칭의 고유 ID. null이 아닌 유효한 값이어야 합니다.
     * @param memberId   삭제를 요청하는 멤버의 고유 ID. null이 아닌 유효한 값이어야 합니다.
     * @throws IllegalArgumentException  matchingId 또는 memberId가 null일 경우 발생합니다
     * @throws MatchingNotFoundException 주어진 matchingId에 해당하는 매칭이 존재하지 않을 경우 발생합니다.
     */
    @Override
    @Transactional
    public void deleteMatching(Long matchingId, Long memberId) {

        Matching matching = matchingRepository.findByMatchingId(matchingId).orElseThrow(() ->
                new MatchingNotFoundException(MATCHING_NOT_FOUND));

        matching.delete(memberId); // 도메인 메서드 호출
        matchingRepository.save(matching); // soft delete 처리 -> DB에 저장된 상태만 유지
    }

    private void updateMatchingFields(MatchingUpdateRequest request,
                                      Matching matching) {
        matching.updateMostActiveTime(request.getMostActiveTime());
        matching.updateLevel(request.getLevel());
        matching.updateAge(request.getAge());
        matching.updateAttendance(request.getIsAttending());
        matching.updateIntroduction(request.getIntroduction());
        matching.updateStatus(request.getStatus());
    }

    private void logging(Category category, Long memberId, List<MatchingResponse> responses) {
        if (responses.isEmpty()) {
            log.info("[MATCH] 해당 카테고리에 매칭 정보가 없습니다. - category: {}, memberId: {}",
                    category, memberId);
        } else {
            log.info("[MATCH] 카테고리별 회원 매칭 조회 완료 - category: {}, memberId: {}, 조회된 건수: {}",
                    category, memberId, responses.size());
        }
    }

    private Matching createNewDomain(MatchingRequest request,
                                     List<Matching> existingMatchings) {
        return Matching.createNew(
                request.getMemberId(),
                request.getCategory(),
                request.getMostActiveTime(),
                request.getLevel(),
                request.getAge(),
                request.getIsAttending(),
                request.getIntroduction(),
                existingMatchings
        );
    }
}
