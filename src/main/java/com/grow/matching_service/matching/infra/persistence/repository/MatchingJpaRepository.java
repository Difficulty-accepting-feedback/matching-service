package com.grow.matching_service.matching.infra.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grow.matching_service.matching.infra.persistence.entity.MatchingJpaEntity;

public interface MatchingJpaRepository
	extends JpaRepository<MatchingJpaEntity, Long> {
	List<MatchingJpaEntity> findByMemberId(Long memberId);
}