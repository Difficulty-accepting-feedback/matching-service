package com.grow.matching_service.matching.domain.model;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.Objects;

import com.grow.matching_service.matching.domain.enums.Category;

@Getter
public class Matching {
	private final Long matchingId;
	private final Long memberId;
	private Category category;
	private LocalDateTime mostActiveTime;
	private Boolean isAttending;
	private String introduction;

	private Matching(Long matchingId,
		Long memberId,
		Category category, // 추후에 DB 연결 후 교체 필요
		LocalDateTime mostActiveTime,
		Boolean isAttending,
		String introduction) {
		this.matchingId      = matchingId;
		this.memberId        = memberId;
		this.category        = category;
		this.mostActiveTime  = mostActiveTime;
		this.isAttending     = isAttending;
		this.introduction    = introduction;
	}

	public static Matching create(Long memberId,
		Category category,
		LocalDateTime mostActiveTime,
		Boolean isAttending,
		String introduction) {
		return new Matching(
			null,
			memberId,
			category,
			mostActiveTime,
			isAttending,
			introduction
		);
	}

	public void updateCategory(Category newCategory) {
		this.category = newCategory;
	}

	public void updateMostActiveTime(LocalDateTime newTime) {
		this.mostActiveTime = newTime;
	}

	public void updateAttendance(boolean attending) {
		this.isAttending = attending;
	}

	public void updateIntroduction(String newIntro) {
		this.introduction = newIntro;
	}

	public static Matching of(Long matchingId,
		Long memberId,
		Category category,
		LocalDateTime mostActiveTime,
		Boolean isAttending,
		String introduction) {
		return new Matching(
			matchingId,
			memberId,
			category,
			mostActiveTime,
			isAttending,
			introduction
		);
	}
}
