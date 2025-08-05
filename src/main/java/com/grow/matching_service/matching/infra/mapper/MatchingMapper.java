package com.grow.matching_service.matching.infra.mapper;

import com.grow.matching_service.matching.domain.model.Matching;
import com.grow.matching_service.matching.infra.entity.MatchingJpaEntity;

import static com.grow.matching_service.matching.infra.entity.MatchingJpaEntity.*;

public class MatchingMapper {

	/**
	 * 엔티티 → 도메인 변환 (toDomain)
	 * DB로부터 조회한 데이터를 도메인 객체로 가져와 비즈니스 로직에서 사용
	 */
	public static Matching toDomain(MatchingJpaEntity entity) {
		return Matching.loadExisting(
				entity.getMatchingId(),
				entity.getMemberId(),
				entity.getCategory(),
				entity.getMostActiveTime(),
				entity.getLevel(),
				entity.getAge(),
				entity.getIsAttending(),
				entity.getIntroduction(),
				entity.getVersion(),
				entity.getStatus()
		);
	}

	/**
	 * 도메인 → 엔티티 변환(toEntity)
	 * 새로운 도메인 객체를 생성하거나 변경된 도메인 상태를 DB에 반영
	 */
	public static MatchingJpaEntity toEntity(Matching domain) {
		MatchingJpaEntityBuilder builder = builder()
				.memberId(domain.getMemberId())
				.category(domain.getCategory())
				.mostActiveTime(domain.getMostActiveTime())
				.level(domain.getLevel())
				.age(domain.getAge())
				.isAttending(domain.getIsAttending())
				.introduction(domain.getIntroduction())
				.version(domain.getVersion())
				.status(domain.getStatus());

		// ID 조건 설정: null이 아니면 업데이트용으로 ID 추가
		// null 일 경우에는 새로운 엔티티 생성
		if (domain.getMatchingId() != null) {
			builder.matchingId(domain.getMatchingId());
		}

		return builder.build();
	}
}
