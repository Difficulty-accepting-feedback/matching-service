package com.grow.matching_service.matching.domain.repository;

import java.util.List;
import java.util.Optional;

import com.grow.matching_service.matching.domain.enums.Category;
import com.grow.matching_service.matching.domain.model.Matching;

public interface MatchingRepository {
	Matching save(Matching matching);
	List<Matching> findByMemberId(Long memberId);
	List<Matching> findByCategoryAndMemberId(Category category, Long memberId);
	Optional<Matching> findByMatchingId(Long matchingId);
}