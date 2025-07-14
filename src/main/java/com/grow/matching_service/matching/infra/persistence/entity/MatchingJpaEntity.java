package com.grow.matching_service.matching.infra.persistence.entity;

import java.time.LocalDateTime;

import com.grow.matching_service.matching.domain.enums.Category;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "matching")
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
	private LocalDateTime mostActiveTime;

	@Column(nullable = false)
	private Boolean isAttending;

	@Column(columnDefinition = "text")
	private String introduction;

	@Builder
	public MatchingJpaEntity(Long memberId,
							 Category category,
							 LocalDateTime mostActiveTime,
							 Boolean isAttending,
							 String introduction
	) {
		this.memberId = memberId;
		this.category = category;
		this.mostActiveTime = mostActiveTime;
		this.isAttending = isAttending;
		this.introduction = introduction;
	}
}