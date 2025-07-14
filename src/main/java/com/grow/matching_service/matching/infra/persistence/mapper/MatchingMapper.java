package com.grow.matching_service.matching.infra.persistence.mapper;

import com.grow.matching_service.matching.domain.model.Matching;
import com.grow.matching_service.matching.infra.persistence.entity.MatchingJpaEntity;

public class MatchingMapper {

	// 엔티티를 도메인으로 변경
	public static Matching toDomain(MatchingJpaEntity entity) {
		return Matching.of(
			entity.getMatchingId(),
			entity.getMemberId(),
			entity.getCategory(),
			entity.getMostActiveTime(),
			entity.getIsAttending(),
			entity.getIntroduction()
		);
	}

	// 도메인을 엔티티로 변경
	public static MatchingJpaEntity toEntity(Matching domain) {
		return MatchingJpaEntity.builder()
			.memberId(domain.getMemberId())
			.category(domain.getCategory())
			.mostActiveTime(domain.getMostActiveTime())
			.isAttending(domain.getIsAttending())
			.introduction(domain.getIntroduction())
			.build();
	}
}
