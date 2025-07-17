package com.grow.matching_service.matching.application.event;

import com.grow.matching_service.matching.infra.dto.MatchingResult;
import com.grow.matching_service.matching.domain.dto.event.MatchingSavedEvent;
import com.grow.matching_service.matching.infra.entity.MatchingJpaEntity;
import com.grow.matching_service.matching.infra.repository.MatchingQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TODO DTO 를 받아서 사용하는 방식으로 코드 수정 필요함
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingEventHandler {

    private final MatchingQueryRepository queryRepository;

    @Async // 트랜잭션 도입 필요할 시 트랜잭션 경계 고려 필요
    @EventListener
    public void handleMatchingSaved(MatchingSavedEvent event) {
        MatchingJpaEntity saved = event.getEntity();
        List<MatchingResult> matchingUsers = queryRepository.findMatchingUsers(saved);

        // 빈 리스트 추출 시 예외 처리
        if (matchingUsers.isEmpty()) {
            log.info("[MATCH] 매칭 대상이 없습니다. memberId: {}",
                    saved.getMemberId());
            return;
        }

        // TODO: 비즈니스 로직 처리 (알림 발송, 집계 or 사용자에게 매칭 정보 전송 등)
        log.info("[MATCH] 가장 유사한 조건의 사용자 : {}",
                matchingUsers.getFirst().getMemberId());
    }


}
