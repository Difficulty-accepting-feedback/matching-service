package com.grow.matching_service.matching.domain.dto.event;

import com.grow.matching_service.matching.infra.dto.MatchingQueryDto;
import com.grow.matching_service.matching.infra.entity.MatchingJpaEntity;
import org.springframework.context.ApplicationEvent;

/**
 * 커스텀 도메인 이벤트
 */
public class MatchingSavedEvent extends ApplicationEvent {

    private final MatchingQueryDto matchingQueryDto;

    public MatchingSavedEvent(MatchingQueryDto matchingQueryDto) {
        super(matchingQueryDto);
        this.matchingQueryDto = matchingQueryDto;
    }

    public MatchingQueryDto getDto() {
        return matchingQueryDto;
    }
}
