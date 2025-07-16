package com.grow.matching_service.matching.application.event;

import com.grow.matching_service.matching.infra.persistence.entity.MatchingJpaEntity;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

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
