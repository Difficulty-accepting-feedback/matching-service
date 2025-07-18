package com.grow.matching_service.matching.infra.repository;

import java.util.List;

import com.grow.matching_service.matching.domain.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import com.grow.matching_service.matching.infra.entity.MatchingJpaEntity;

public interface MatchingJpaRepository
	extends JpaRepository<MatchingJpaEntity, Long> {
	List<MatchingJpaEntity> findByMemberId(Long memberId);

	/**
	 * 카테고리와 회원 ID를 기준으로 매칭 정보를 조회합니다. (최신순 정렬)
	 * @param category 조회할 카테고리
	 * @param memberId 조회할 회원 ID
	 * @return 매칭 정보 엔티티 리스트
	 */
    List<MatchingJpaEntity> findByCategoryAndMemberIdOrderByMatchingIdDesc(Category category,
																		   Long memberId);
}