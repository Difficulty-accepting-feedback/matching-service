package com.grow.matching_service.matching.infra.entity;

import com.grow.matching_service.matching.domain.model.MatchingStatus;
import com.grow.matching_service.matching.infra.event.MatchingEntityListener;
import com.grow.matching_service.matching.domain.enums.Age;
import com.grow.matching_service.matching.domain.enums.Category;

import com.grow.matching_service.matching.domain.enums.Level;
import com.grow.matching_service.matching.domain.enums.MostActiveTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@Table(name = "matching")
@EntityListeners(MatchingEntityListener.class)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long matchingId;

	@Column(nullable = false)
	private Long memberId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Category category;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MostActiveTime mostActiveTime;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Level level;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Age age;

	@Column(nullable = false)
	private Boolean isAttending; // 오프라인 모임 참석 여부

	@Column(columnDefinition = "text")
	private String introduction;

	@Enumerated(EnumType.STRING)
	private MatchingStatus status; // 매칭 삭제 여부 (ACTIVE, DELETED)

	@Version
	private Long version; // Optimistic Lock 처리를 위한 버전 관리
}