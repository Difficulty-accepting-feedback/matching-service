package com.grow.matching_service.matching.infra.repository;

import com.grow.matching_service.matching.domain.dto.MatchingResult;
import com.grow.matching_service.matching.infra.entity.MatchingJpaEntity;
import com.grow.matching_service.matching.infra.entity.QMatchingJpaEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <h2>사용자 매칭 전용 QueryDSL 레포지토리</h2>
 *
 * <p>기준 사용자가 속한 카테고리 내에서 아래 규칙으로 유사도 점수를 계산해
 * 1 점 이상인 사용자 목록을 반환함 </p>
 *
 * <ol>
 *   <li>카테고리: 반드시 동일 (조건 필수)</li>
 *   <li>mostActiveTime · level · age · isAttending
 *       – 속성이 일치할 때마다 1 점 가산 (최대 4 점)</li>
 *   <li>본인은 제외하고, 점수 내림차순 정렬</li>
 * </ol>
 *
 * @author sun
 * @since 2025.07.15
 */
@Repository
@RequiredArgsConstructor
public class MatchingQueryRepositoryImpl implements MatchingQueryRepository {

    private final JPAQueryFactory factory;

    /**
     * 기준 엔티티와 유사한 사용자 목록을 조회한다.
     *
     * @param reference 기준이 되는 {@link MatchingJpaEntity}
     * @return 유사도 점수(1 ~ 4점)와 함께 정렬된 결과
     */
    @Override
    public List<MatchingResult> findMatchingUsers(MatchingJpaEntity reference) {
        QMatchingJpaEntity target = QMatchingJpaEntity.matchingJpaEntity;

        // 0~4점 범위의 동적 점수 계산 (CaseBuilder 사용)
        NumberExpression<Integer> score = buildScoreExpression(reference, target);

        return factory
                .select(Projections.constructor(MatchingResult.class,
                        target.memberId,
                        target.category,
                        target.mostActiveTime,
                        target.level,
                        target.age,
                        target.isAttending,
                        target.introduction,
                        score
                ))
                .from(target)
                .where(
                        target.memberId.ne(reference.getMemberId()),   // 본인 제외
                        target.category.eq(reference.getCategory()),  // 카테고리 강제 일치
                        score.goe(1)                                   // 1점 이상
                )
                .orderBy(score.desc())
                .fetch();
    }

    /**
     * 각 속성 일치 시 1 점을 가산하는 점수 식을 생성한다.
     *
     * @param ref    기준 엔티티
     * @param target Q타입 엔티티
     * @return 0~4점 {@link NumberExpression}
     */
    private NumberExpression<Integer> buildScoreExpression(MatchingJpaEntity ref,
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
}
