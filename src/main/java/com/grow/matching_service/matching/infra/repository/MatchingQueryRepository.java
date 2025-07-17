package com.grow.matching_service.matching.infra.repository;

import com.grow.matching_service.matching.infra.dto.MatchingResult;
import com.grow.matching_service.matching.infra.entity.MatchingJpaEntity;

import java.util.List;

public interface MatchingQueryRepository {
    // 조건으로 맞는 엔티티를 찾기 위해 쿼리문 사용
    List<MatchingResult> findMatchingUsers(MatchingJpaEntity entity);
}
