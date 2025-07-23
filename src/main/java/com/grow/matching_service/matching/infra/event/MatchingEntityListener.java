package com.grow.matching_service.matching.infra.event;

import com.grow.matching_service.matching.domain.dto.event.MatchingSavedEvent;
import com.grow.matching_service.matching.infra.dto.MatchingQueryDto;
import com.grow.matching_service.matching.infra.entity.MatchingJpaEntity;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * JPA 엔티티의 저장 또는 수정 후 이벤트를 수집하고 새로운 이벤트를 발행하기 위한 클래스 (쿼리 실행)
 */
@Component
public class MatchingEntityListener {

    private final ApplicationEventPublisher publisher;

    public MatchingEntityListener(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @PostPersist // 저장 후
    @PostUpdate // 수정 후
    public void onAfterSave(MatchingJpaEntity entity) {
        // DTO 생성 (matchingId와 introduction 제외 -> 쿼리에 필요하지 않음)
        MatchingQueryDto dto = MatchingQueryDto.builder()
                .memberId(entity.getMemberId())
                .category(entity.getCategory())
                .mostActiveTime(entity.getMostActiveTime())
                .level(entity.getLevel())
                .age(entity.getAge())
                .isAttending(entity.getIsAttending())
                .build();

        // DTO를 포함한 이벤트 발행
        publisher.publishEvent(new MatchingSavedEvent(dto));
    }
}
