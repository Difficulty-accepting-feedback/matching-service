package com.grow.matching_service.matching.infra.persistence.mapper;

import com.grow.matching_service.matching.domain.model.Matching;
import com.grow.matching_service.matching.infra.persistence.entity.MatchingJpaEntity;

public class MatchingMapper {

	public static Matching toDomain(MatchingJpaEntity e) {
		return Matching.of(
			e.getMatchingId(),
			e.getMemberId(),
			e.getCategory(),
			e.getMostActiveTime(),
			e.getIsAttending(),
			e.getIntroduction()
		);
	}

	public static MatchingJpaEntity toEntity(Matching d) {
		return MatchingJpaEntity.builder()
			.matchingId(d.getMatchingId())
			.memberId(d.getMemberId())
			.category(d.getCategory())
			.mostActiveTime(d.getMostActiveTime())
			.isAttending(d.getIsAttending())
			.introduction(d.getIntroduction())
			.build();
	}
}
