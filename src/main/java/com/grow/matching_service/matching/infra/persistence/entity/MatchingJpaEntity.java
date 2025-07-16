package com.grow.matching_service.matching.infra.persistence.entity;

import com.grow.matching_service.matching.application.event.MatchingEntityListener;
import com.grow.matching_service.matching.domain.enums.Age;
import com.grow.matching_service.matching.domain.enums.Category;

import com.grow.matching_service.matching.domain.enums.Level;
import com.grow.matching_service.matching.domain.enums.MostActiveTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
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
	private MostActiveTime mostActiveTime;

	@Column(nullable = false)
	private Level level;

	@Column(nullable = false)
	private Age age;

	@Column(nullable = false)
	private Boolean isAttending; // 오프라인 모임 참석 여부

	@Column(columnDefinition = "text")
	private String introduction;

	@Builder
	public MatchingJpaEntity(Long memberId,
							 Category category,
							 MostActiveTime mostActiveTime,
							 Level level,
							 Age age,
							 Boolean isAttending,
							 String introduction
	) {
		this.memberId = memberId;
		this.category = category;
		this.mostActiveTime = mostActiveTime;
		this.level = level;
		this.age = age;
		this.isAttending = isAttending;
		this.introduction = introduction;
	}
}