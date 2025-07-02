package com.grow.matching_service.matching.domain.repository;

import java.util.List;
import java.util.Optional;

import com.grow.matching_service.matching.domain.model.Matching;

public interface MatchingRepository {
	Matching save(Matching matching);
	Optional<Matching> findById(Long matchingId);
	List<Matching> findByMemberId(Long memberId);
	void delete(Matching matching);
}