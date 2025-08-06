package com.grow.matching_service.matching.infra.repository;

import com.grow.matching_service.matching.domain.enums.Age;
import com.grow.matching_service.matching.domain.enums.MatchingStatus;
import com.grow.matching_service.matching.infra.dto.MatchingQueryDto;
import com.grow.matching_service.matching.infra.dto.MatchingResult;
import com.grow.matching_service.matching.infra.entity.MatchingJpaEntity;
import com.grow.matching_service.matching.infra.entity.QMatchingJpaEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;

/**
 * <h2>사용자 매칭 전용 QueryDSL 레포지토리</h2>
 *
 * <p>기준 사용자가 속한 카테고리 내에서 아래 규칙으로 유사도 점수를 계산해
 * 1 점 이상인 사용자 목록을 반환함. 결과가 20명 이하인 경우 그대로 반환하나,
 * 20명 초과 시 Redis 캐시에서 신뢰도 점수와 구독 여부를 조회하여 재정렬 후 상위 20명만 선별함.</p>
 *
 * <ol>
 *   <li>카테고리: 반드시 동일 (조건 필수)</li>
 *   <li>mostActiveTime · level · age · isAttending
 *       – 속성이 일치할 때마다 1 점 가산 (최대 4 점)</li>
 *   <li>본인은 제외하고, 점수 내림차순 정렬</li>
 *   <li>결과가 20명 초과 시 추가 정렬:
 *       <ul>
 *         <li>Redis 신뢰도 점수 내림차순 (높은 점수 우선)</li>
 *         <li>신뢰도 점수가 동일할 경우, 구독 여부 내림차순 (구독한 사용자 우선)</li>
 *       </ul>
 *       상위 20명만 반환 (Redis 키: "member:trust:score:{memberId}" 및 "member:subscription:{memberId}")
 *   </li>
 * </ol>
 *
 * @since 2025.07.15
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class MatchingQueryRepositoryImpl implements MatchingQueryRepository {

    private final JPAQueryFactory factory;
    private final RedisTemplate<String, Boolean> booleanRedisTemplate; // 구독 여부 캐싱
    private final RedisTemplate<String, Double> doubleRedisTemplate; // 신뢰도 점수 캐싱

    public static final String TRUST_KEY = "member:trust:score:";
    public static final String SUB_KEY = "member:subscription:";

    /**
     * 기준 엔티티와 유사한 사용자 목록을 조회한다.
     *
     * @param reference 기준이 되는 {@link MatchingJpaEntity}
     * @return 유사도 점수(1 ~ 4점)와 함께 정렬된 결과
     */
    @Override
    public List<MatchingResult> findMatchingUsers(MatchingQueryDto reference) {
        QMatchingJpaEntity target = QMatchingJpaEntity.matchingJpaEntity;

        // 동적 age 조건 생성
        BooleanExpression ageCondition = (reference.getAge() == Age.NONE)
                ? null // NONE 이면 조건 무시
                : target.age.eq(reference.getAge()); // NONE이 아니면 같은 age만 필터링

        // 0~4점 범위의 동적 점수 계산 (CaseBuilder 사용)
        NumberExpression<Integer> score = buildScoreExpression(reference, target);

        List<MatchingResult> candidates = factory
                .select(Projections.constructor(MatchingResult.class,
                        target.memberId,
                        target.category,
                        target.mostActiveTime,
                        target.level,
                        target.age,
                        target.isAttending,
                        target.introduction,
                        target.status,
                        score
                ))
                .from(target)
                .where(
                        target.memberId.ne(reference.getMemberId()),    // 본인 제외
                        target.category.eq(reference.getCategory()),    // 카테고리 강제 일치
                        ageCondition,                                   // age 강제 일치
                        score.goe(1),                             // 1점 이상
                        target.status.eq(MatchingStatus.ACTIVE)         // 활성화된 유저만 조회
                )
                .orderBy(score.desc()) // 점수 내림차순 정렬 (높은 순서부터)
                .fetch();

        // 20명 이상일 경우 redis 기반으로 정렬
        if (candidates.size() > 20) {
            log.info("[Matching-Query] 20명 이상의 유사도 점수가 있는 사용자가 존재합니다. Redis 캐시를 조회합니다.");
            candidates = sortCandidatesByTrustAndSubscription(candidates);
            candidates = candidates.subList(0, Math.min(20, candidates.size())); // 상위 20명 추출
        }

        return candidates;
    }

    /**
     * 각 속성 일치 시 1 점을 가산하는 점수 식을 생성한다.
     *
     * @param ref    기준 엔티티
     * @param target Q타입 엔티티
     * @return 0~4점 {@link NumberExpression}
     */
    private NumberExpression<Integer> buildScoreExpression(MatchingQueryDto ref,
                                                           QMatchingJpaEntity target) {
        return new CaseBuilder()
                .when(target.mostActiveTime.eq(ref.getMostActiveTime())).then(1).otherwise(0)
                .add(new CaseBuilder()
                        .when(target.level.eq(ref.getLevel())).then(1).otherwise(0))
                .add(new CaseBuilder()
                        .when(target.age.eq(ref.getAge())).then(1).otherwise(0))
                .add(new CaseBuilder()
                        .when(target.isAttending.eq(ref.getIsAttending())).then(1).otherwise(0));
    }

    /**
     * Redis 캐시에서 각 후보의 신뢰도 점수(trust score)와 구독 여부(subscription status)를 조회하여,
     * 후보 목록을 정렬합니다. 정렬 기준은 다음과 같습니다:
     * <ul>
     *     <li>신뢰도 점수 내림차순 (높은 점수가 우선)</li>
     *     <li>신뢰도 점수가 동일할 경우, 구독 여부 내림차순 (구독한 사용자(true)가 우선)</li>
     * </ul>
     * Redis 키는 다음과 같이 가정합니다:
     * <ul>
     *     <li>신뢰도 점수 키: "member:trust:score:{memberId}" (값: Double)</li>
     *     <li>구독 여부 키: "member:subscription:{memberId}" (값: Boolean)</li>
     * </ul>
     * 캐시 미스(값이 없을 경우) 신뢰도 점수는 0.0으로, 구독 여부는 false로 기본 처리합니다.
     *
     * @param candidates 정렬할 MatchingResult 후보 목록 (각 항목에 memberId가 포함되어 있어야 함)
     * @return 정렬된 MatchingResult 목록 (원본 목록과 동일한 타입)
     * @see MatchingResult MatchingResult DTO 클래스
     * @see CandidateWithScore 내부 헬퍼 클래스 (정렬을 위한 임시 wrapper)
     */
    // TODO Redis 연결 실패 또는 조회 오류 시 발생할 수 있음 (try-catch로 변경하는 게 나을지?)
    private List<MatchingResult> sortCandidatesByTrustAndSubscription(List<MatchingResult> candidates) {
        return candidates.stream()
                .map(candidate -> {
                    Long memberId = candidate.getMemberId();

                    // Redis 에서 신뢰도 점수 가져오기 (없으면 0으로 기본값 설정)
                    Double trustScore = doubleRedisTemplate
                            .opsForValue()
                            .get(TRUST_KEY + memberId);
                    if (trustScore == null) {
                        trustScore = 0.0;
                        log.warn("[Matching-Query] Redis 신뢰도 점수 없음: {}", memberId);
                    }

                    // Redis 에서 구독 여부 가져오기 (없으면 false)
                    Boolean isSubscribed = booleanRedisTemplate
                            .opsForValue()
                            .get(SUB_KEY + memberId);
                    if (isSubscribed == null) {
                        isSubscribed = false;
                        log.warn("[Matching-Query] Redis 구독 여부 없음: {}", memberId);
                    }

                    // 임시 CandidateWithScore 객체 생성: 정렬을 위해 신뢰도와 구독 정보를 함께 묶음
                    return new CandidateWithScore(
                            candidate,
                            trustScore,
                            isSubscribed
                    );
                })
                .sorted(Comparator.comparingDouble(CandidateWithScore::getTrustScore)
                        .reversed() // 1차 정렬: 신뢰도 점수 내림차순 (높은 점수 우선)
                        .thenComparing(
                                CandidateWithScore::isSubscribed,
                                Comparator.reverseOrder()
                        ) // 2차 정렬: 구독 여부 내림차순 (true가 false보다 우선)
                )
                .map(CandidateWithScore::getCandidate) // 정렬 후 원본 MatchingResult만 추출 (wrapper 제거)
                .toList(); // 최종 리스트로 변환
    }

    /**
     * 정렬을 위한 임시 wrapper 클래스. MatchingResult에 신뢰도 점수와 구독 여부를 추가로 저장합니다.
     * 이 클래스는 sortCandidatesByTrustAndSubscription 메서드 내에서만 사용되며, 메모리 효율을 위해 static으로 정의.
     *
     * @see MatchingResult 원본 DTO
     */
    @Getter
    @AllArgsConstructor
    private static class CandidateWithScore {
        private final MatchingResult candidate;
        private final double trustScore;
        private final boolean isSubscribed;
    }
}
