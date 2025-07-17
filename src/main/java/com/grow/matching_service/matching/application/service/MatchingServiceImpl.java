package com.grow.matching_service.matching.application.service;

import com.grow.matching_service.matching.persistence.dto.MatchingRequest;
import com.grow.matching_service.matching.domain.model.Matching;
import com.grow.matching_service.matching.domain.repository.MatchingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        // 도메인 생성
        Matching matching = createNewDomain(request);

        // 레포지토리에 저장
        matchingRepository.save(matching);
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
