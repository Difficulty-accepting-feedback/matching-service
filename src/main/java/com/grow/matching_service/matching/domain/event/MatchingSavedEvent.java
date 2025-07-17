package com.grow.matching_service.matching.domain.event;

import com.grow.matching_service.matching.infra.entity.MatchingJpaEntity;
import org.springframework.context.ApplicationEvent;

/**
 * 커스텀 도메인 이벤트
 */
public class MatchingSavedEvent extends ApplicationEvent {

    private final MatchingJpaEntity entity;

    public MatchingSavedEvent(MatchingJpaEntity entity) {
        super(entity);
        this.entity = entity;
    }

    public MatchingJpaEntity getEntity() {
        return entity;
    }
}
