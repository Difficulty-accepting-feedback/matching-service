package com.grow.matching_service.matching.infra.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.grow.matching_service.matching.domain.model.Matching;
import com.grow.matching_service.matching.domain.repository.MatchingRepository;
import com.grow.matching_service.matching.infra.entity.MatchingJpaEntity;
import com.grow.matching_service.matching.infra.mapper.MatchingMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MatchingRepositoryImpl implements MatchingRepository {

	private final MatchingJpaRepository matchingJpaRepository;

	@Override
	public Matching save(Matching matching) {
		MatchingJpaEntity saved = matchingJpaRepository.save(MatchingMapper.toEntity(matching));
		return MatchingMapper.toDomain(saved);
	}

	@Override
	public Optional<Matching> findById(Long matchingId) {
		return matchingJpaRepository.findById(matchingId)
			.map(MatchingMapper::toDomain);
	}

	@Override
	public List<Matching> findByMemberId(Long memberId) {
		return matchingJpaRepository.findByMemberId(memberId).stream()
			.map(MatchingMapper::toDomain)
			.collect(Collectors.toList());
	}

	@Override
	public void delete(Matching matching) {
		matchingJpaRepository.delete(MatchingMapper.toEntity(matching));
	}
}