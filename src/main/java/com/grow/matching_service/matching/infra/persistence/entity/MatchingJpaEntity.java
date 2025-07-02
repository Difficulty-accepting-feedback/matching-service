package com.grow.matching_service.matching.infra.persistence.entity;

import java.time.LocalDateTime;

import com.grow.matching_service.matching.domain.enums.Category;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
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
}