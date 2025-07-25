package com.grow.matching_service.matching.infra.persistence.repository;

import com.grow.matching_service.matching.domain.enums.Age;
import com.grow.matching_service.matching.domain.enums.Category;
import com.grow.matching_service.matching.domain.enums.Level;
import com.grow.matching_service.matching.domain.enums.MostActiveTime;
import com.grow.matching_service.matching.infra.dto.MatchingQueryDto;
import com.grow.matching_service.matching.infra.dto.MatchingResult;
import com.grow.matching_service.matching.infra.entity.MatchingJpaEntity;
import com.grow.matching_service.matching.infra.repository.MatchingQueryRepositoryImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MatchingTestV2 {

    @Autowired
    private EntityManager em;

    @Autowired
    private MatchingQueryRepositoryImpl repository;

    private MatchingJpaEntity baseEntityWithAge;
    private MatchingJpaEntity baseEntityWithoutAge;

    private MatchingQueryDto dtoWithAge;
    private MatchingQueryDto dtoWithoutAge;

    @BeforeEach
    void setUp() {
        // 기준 매칭 엔티티 저장
        baseEntityWithoutAge = MatchingJpaEntity.builder()
                .memberId(1L)
                .category(Category.STUDY)
                .mostActiveTime(MostActiveTime.MORNING)
                .level(Level.SEED)
                .age(Age.NONE) // 나이 조건 없음
                .isAttending(true)
                .introduction("base")
                .build();
        em.persist(baseEntityWithoutAge);

        baseEntityWithAge = MatchingJpaEntity.builder()
                .memberId(2L)
                .category(Category.HOBBY)
                .mostActiveTime(MostActiveTime.EVENING)
                .level(Level.BLOOMING)
                .age(Age.TWENTIES) // 나이 조건 있음
                .isAttending(true)
                .introduction("base")
                .build();
        em.persist(baseEntityWithAge);
        em.flush();
        em.clear();

        dtoWithAge = MatchingQueryDto.builder()
                .memberId(1L)  // baseEntity의 memberId
                .category(Category.STUDY)  // baseEntity의 category
                .mostActiveTime(MostActiveTime.MORNING)  // baseEntity의 mostActiveTime
                .level(Level.SEED)  // baseEntity의 level
                .age(Age.NONE)  // baseEntity의 age
                .isAttending(true)  // baseEntity의 isAttending
                .build();

        dtoWithoutAge = MatchingQueryDto.builder()
                .memberId(2L)  // baseEntity의 memberId
                .category(Category.HOBBY)  // baseEntity의 category
                .mostActiveTime(MostActiveTime.EVENING)  // baseEntity의 mostActiveTime
                .level(Level.BLOOMING)  // baseEntity의 level
                .age(Age.TWENTIES)  // baseEntity의 age
                .isAttending(true)  // baseEntity의 isAttending
                .build();
    }

    @Test
    @DisplayName("BaseEntity: 나이 조건 NONE 인 경우 -> 전체 회원을 대상으로 검색한다")
    void testFindMatchingUsersAgeNone() {
        // 엔티티 저장
        MatchingJpaEntity age20 = MatchingJpaEntity.builder()
                .memberId(2L)
                .category(Category.STUDY)
                .mostActiveTime(MostActiveTime.MORNING)
                .level(Level.SEED)
                .age(Age.TWENTIES)
                .isAttending(true)
                .introduction("match1")
                .build();
        em.persist(age20);

        MatchingJpaEntity age30 = MatchingJpaEntity.builder()
                .memberId(3L)
                .category(Category.STUDY)
                .mostActiveTime(MostActiveTime.MORNING)
                .level(Level.SEED)
                .age(Age.THIRTIES) // 다른 조건은 다 동일한데 나이만 다름
                .isAttending(true)
                .introduction("match2")
                .build();
        em.persist(age30);

        MatchingJpaEntity age40 = MatchingJpaEntity.builder()
                .memberId(4L)
                .category(Category.STUDY)
                .mostActiveTime(MostActiveTime.MORNING)
                .level(Level.SEED)
                .age(Age.FORTIES)
                .isAttending(true)
                .introduction("match3")
                .build();
        em.persist(age40);

        em.flush();
        em.clear();

        List<MatchingResult> results = repository.findMatchingUsers(dtoWithAge);
        assertThat(results.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("나이 조건 NONE 아닌 경우 동일 나이만 조회 테스트")
    void testFindMatchingUsersAgeSpecific() throws Exception {
        MatchingJpaEntity age20 = MatchingJpaEntity.builder()
                .memberId(3L)
                .category(Category.HOBBY)
                .mostActiveTime(MostActiveTime.MORNING)
                .level(Level.SEED)
                .age(Age.TWENTIES) // 동일 조건
                .isAttending(true)
                .introduction("match1")
                .build();
        em.persist(age20);

        MatchingJpaEntity age30 = MatchingJpaEntity.builder()
                .memberId(4L)
                .category(Category.HOBBY)
                .mostActiveTime(MostActiveTime.MORNING)
                .level(Level.SEED)
                .age(Age.THIRTIES) // 20대 != 30대
                .isAttending(true)
                .introduction("match2")
                .build();
        em.persist(age30);

        MatchingJpaEntity age40 = MatchingJpaEntity.builder()
                .memberId(5L)
                .category(Category.HOBBY)
                .mostActiveTime(MostActiveTime.MORNING)
                .level(Level.SEED)
                .age(Age.FORTIES) // 20대 != 40대
                .isAttending(true)
                .introduction("match3")
                .build();
        em.persist(age40);

        em.flush();
        em.clear();

        // 20대만 조회되어야 함 -> memberId=2만 반환됨
        List<MatchingResult> results = repository.findMatchingUsers(dtoWithoutAge);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.getFirst().getMemberId()).isEqualTo(3L);
    }
}
