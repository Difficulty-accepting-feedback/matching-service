package com.grow.matching_service.matching.infra.persistence.repository;

import com.grow.matching_service.matching.domain.enums.MatchingStatus;
import com.grow.matching_service.matching.infra.dto.MatchingQueryDto;
import com.grow.matching_service.matching.infra.dto.MatchingResult;
import com.grow.matching_service.matching.domain.enums.Age;
import com.grow.matching_service.matching.domain.enums.Category;
import com.grow.matching_service.matching.domain.enums.Level;
import com.grow.matching_service.matching.infra.entity.MatchingJpaEntity;
import com.grow.matching_service.matching.infra.repository.MatchingQueryRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.grow.matching_service.matching.domain.enums.Age.TWENTIES;
import static com.grow.matching_service.matching.domain.enums.MostActiveTime.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Transactional
@SpringBootTest
@ActiveProfiles("test")
class MatchingQueryRepositoryImplTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private MatchingQueryRepositoryImpl repository;

    private MatchingJpaEntity baseEntity;
    MatchingQueryDto dto;

    @BeforeEach
    void setUp() {
        // 기준 매칭 엔티티 저장
        baseEntity = MatchingJpaEntity.builder()
                .memberId(1L)
                .category(Category.STUDY)
                .mostActiveTime(MORNING)
                .level(Level.SEED)
                .age(TWENTIES)
                .isAttending(true)
                .introduction("base")
                .status(MatchingStatus.ACTIVE)
                .build();
        em.persist(baseEntity);

        em.flush();
        em.clear();

        dto = MatchingQueryDto.builder()
                .memberId(1L)  // baseEntity의 memberId
                .category(Category.STUDY)  // baseEntity의 category
                .mostActiveTime(MORNING)  // baseEntity의 mostActiveTime
                .level(Level.SEED)  // baseEntity의 level
                .age(TWENTIES)  // baseEntity의 age
                .isAttending(true)  // baseEntity의 isAttending
                .build();
    }

    @Test
    @DisplayName("findMatchingUsers: 유사 점수 1 이상인 사용자 반환 및 점수 내림차순 정렬")
    void testFindMatchingUsers_orderedByScore() {
        // given
        // 유사도 높은 매칭 엔티티들 저장
        em.persist(MatchingJpaEntity.builder()
                .memberId(2L)
                .category(baseEntity.getCategory())
                .mostActiveTime(baseEntity.getMostActiveTime())
                .level(baseEntity.getLevel())
                .age(baseEntity.getAge())
                .isAttending(baseEntity.getIsAttending())
                .introduction("match1")
                .status(MatchingStatus.ACTIVE)
                .build()); // 완전 동일

        em.persist(MatchingJpaEntity.builder()
                .memberId(3L)
                .category(baseEntity.getCategory())
                .mostActiveTime(baseEntity.getMostActiveTime())
                .level(Level.BLOOMING) // 점수 차이
                .age(baseEntity.getAge())
                .isAttending(baseEntity.getIsAttending())
                .introduction("match2")
                .status(MatchingStatus.ACTIVE)
                .build());

        em.persist(MatchingJpaEntity.builder()
                .memberId(4L)
                .category(Category.HOBBY) // 불일치
                .mostActiveTime(AFTERNOON) // 불일치
                .level(Level.BLOOMING) // 불일치
                .age(Age.SIXTIES) // 불일치
                .isAttending(false)
                .introduction("match3")
                .status(MatchingStatus.ACTIVE)
                .build());

        em.flush();
        em.clear();

        // when
        List<MatchingResult> results = repository.findMatchingUsers(dto);

        //then
        // memberId=2는 모든 조건 일치 memberId=3는 한 가지가 다르다
        // 일치하지 않는 memberId=4는 제외된다 -> 2개만 반환됨
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getMemberId()).isEqualTo(2L);
        assertThat(results.get(1).getMemberId()).isEqualTo(3L);
        assertThat(results.get(0).getScore()).isEqualTo(4);
        assertThat(results.get(1).getScore()).isEqualTo(3);
    }
}