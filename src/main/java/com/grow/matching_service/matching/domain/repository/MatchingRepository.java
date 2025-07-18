package com.grow.matching_service.matching.domain.repository;

import java.util.List;

import com.grow.matching_service.matching.domain.enums.Category;
import com.grow.matching_service.matching.domain.model.Matching;

public interface MatchingRepository {
	void save(Matching matching);
	List<Matching> findByMemberId(Long memberId);
	List<Matching> findByCategoryAndMemberId(Category category, Long memberId);
}