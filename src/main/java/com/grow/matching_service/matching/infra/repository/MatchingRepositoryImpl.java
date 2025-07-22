package com.grow.matching_service.matching.infra.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.grow.matching_service.matching.domain.enums.Category;
import com.grow.matching_service.matching.infra.entity.MatchingJpaEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import com.grow.matching_service.matching.domain.model.Matching;
import com.grow.matching_service.matching.domain.repository.MatchingRepository;
import com.grow.matching_service.matching.infra.mapper.MatchingMapper;

import lombok.RequiredArgsConstructor;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MatchingRepositoryImpl implements MatchingRepository {

	private final MatchingJpaRepository matchingJpaRepository;

	@Override
	public Matching save(Matching matching) {
		MatchingJpaEntity saved = matchingJpaRepository.save(MatchingMapper.toEntity(matching));
		Matching domain = MatchingMapper.toDomain(saved);
		log.info("[MATCH] 매칭 정보 저장 완료 - memberId: {}", matching.getMemberId());
		return domain;
	}

	@Override
	public List<Matching> findByMemberId(Long memberId) {
		return matchingJpaRepository.findByMemberId(memberId).stream()
				.map(MatchingMapper::toDomain)
				.collect(Collectors.toList());
	}

	/**
	 * 카테고리와 회원 ID를 기준으로 매칭 정보를 조회합니다. (최신순 정렬)
	 * @param category 조회할 카테고리
	 * @param memberId 조회할 회원 ID
	 * @return 매칭 정보 리스트
	 */
	@Override
	public List<Matching> findByCategoryAndMemberId(Category category,
													Long memberId) {
        return matchingJpaRepository
                .findByCategoryAndMemberIdOrderByMatchingIdDesc(category, memberId).stream()
                .map(MatchingMapper::toDomain)
                .collect(Collectors.toList());
	}

	@Override
	public Optional<Matching> findByMatchingId(Long matchingId) {
		return matchingJpaRepository.findById(matchingId)
				.map(MatchingMapper::toDomain);
	}
}