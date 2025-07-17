package com.grow.matching_service.matching.infra.event;

import com.grow.matching_service.matching.domain.dto.event.MatchingSavedEvent;
import com.grow.matching_service.matching.infra.entity.MatchingJpaEntity;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * TODO DTO 를 넘기는 방식으로 코드 수정 필요함
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
        // 저장 or 수정된 엔티티 정보를 담아 이벤트 발행
        publisher.publishEvent(new MatchingSavedEvent(entity));
    }
}
