package com.grow.matching_service.matching.infra.persistence.repository;

import com.grow.matching_service.matching.domain.enums.*;
import com.grow.matching_service.matching.infra.dto.MatchingQueryDto;
import com.grow.matching_service.matching.infra.dto.MatchingResult;
import com.grow.matching_service.matching.infra.entity.MatchingJpaEntity;
import com.grow.matching_service.matching.infra.repository.MatchingQueryRepositoryImpl;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class MatchingQueryRedisTest {

    @Autowired
    private MatchingQueryRepositoryImpl matchingQueryRepository; // 테스트 대상

    @Autowired
    private EntityManager em;

    @Autowired
    private RedisTemplate<String, Boolean> booleanRedisTemplate; // 구독 여부 캐싱

    @Autowired
    private RedisTemplate<String, Double> doubleRedisTemplate; // 신뢰도 점수 캐싱

    private MatchingQueryDto baseDto;

    @BeforeEach
    void setUp() {
        // 각 테스트 전에 Redis 캐시 초기화 (기존 데이터 삭제)
        booleanRedisTemplate.getConnectionFactory().getConnection().flushDb();
        doubleRedisTemplate.getConnectionFactory().getConnection().flushDb();

        // 샘플 Redis 데이터 세팅 (테스트용 멤버 ID: 2~25 가정, 25명 후보 시뮬레이션)
        // 신뢰도 점수와 구독 여부 설정 (예: 높은 신뢰도 우선, 같은 점수 시 구독 우선)
        for (long i = 2; i <= 26; i++) {
            String trustKey = "member:trust:score:" + i;
            String subKey = "member:subscription:" + i;

            // 예시 데이터: ID 2~10: 고신뢰도(90점대), ID 11~25: 저신뢰도(50~26), 구독 random
            int groupIndex = (int) Math.ceil((i - 1) / 2.0); // (2-3: group1, 4-5: group2, ..., 24-25: group12, 26: group13)
            double trustScore;
            if (i <= 10) {
                // 고신뢰 그룹: 그룹별로 99, 98, ..., 95 (감소 단위 1.0) (동점 만들기)
                trustScore = 100 - groupIndex * 1.0;  // 2-3:99, 4-5:98, 6-7:97, 8-9:96, 10:95 (10은 단독, group5)
            } else {
                // 저신뢰 그룹: 그룹별로 59, 58, ..., ~ (11-12:59, 13-14:58, ..., 24-25:52, 26:51)
                int lowGroupIndex = (int) Math.ceil((i - 9) / 2.0);
                trustScore = 60 - lowGroupIndex * 1.0;
            }

            boolean isSubscribed = (i % 2 == 0);  // 짝수 ID: 구독 true, 홀수: false

            log.info("[redis cache] memberId: {}, trustScore: {}, isSubscribed: {}", i, trustScore, isSubscribed);

            doubleRedisTemplate.opsForValue().set(trustKey, trustScore);
            booleanRedisTemplate.opsForValue().set(subKey, isSubscribed);
        }

        baseDto = MatchingQueryDto.builder()
                .memberId(1L)  // 본인 memberId (본인 제외 로직 테스트)
                .category(Category.STUDY)  // 카테고리 예시 (enum 가정)
                .mostActiveTime(MostActiveTime.MORNING)
                .level(Level.SEED)
                .age(Age.NONE)
                .isAttending(true)
                .build();
    }

    @Test
    @DisplayName("매칭된 사용자가 20명 이하인 경우에는 그대로 전부 출력된다")
    void findMatchingV1() throws Exception {
        // Given: 기준 사용자 설정 -> setUp 에서 실행
        // DB에 10명 테스트 데이터 삽입 (유사도 점수 계산을 위한 데이터: 일부 속성 일치)
        for (long i = 2; i <= 11; i++) {
            MatchingJpaEntity entity = MatchingJpaEntity.builder()
                    .memberId(i)
                    .category(Category.STUDY)  // 카테고리 동일
                    .mostActiveTime(MostActiveTime.MORNING) // 일치 -> 무조건 1점은 얻을 수 있음
                    .level(i % 5 == 0 ? Level.SEED : Level.FRUITFUL) // 일부 일치
                    .age(i % 2 == 0 ? Age.TWENTIES : Age.THIRTIES) // 나이 선택하지 않았을 경우에 점수에 포함되지 않음
                    .isAttending(i % 5 == 0)
                    .status(MatchingStatus.ACTIVE)
                    .build();
            em.persist(entity);
        }
        em.flush();  // DB 반영

        Thread.sleep(1000); // 별도 스레드에서 전부 실행될 시간을 기다림

        // When
        List<MatchingResult> results = matchingQueryRepository.findMatchingUsers(baseDto);

        // Then: 20명 이하이므로 캐싱 추가 정렬 없이 그대로 반환 (크기 확인)
        assertThat(results).hasSize(10);  // 정확한 크기 검증 (삽입한 10명)

        // 추가 검증: 유사도 점수 내림차순 정렬 확인
        for (int i = 0; i < results.size() - 1; i++) {
            double currentScore = results.get(i).getScore();  // getScore() 가정 (유사도 점수 필드)
            double nextScore = results.get(i + 1).getScore();
            assertThat(currentScore).isGreaterThanOrEqualTo(nextScore);  // 내림차순 확인
        }

        // 추가 검증: 최소 1점 이상 (쿼리 조건 확인)
        assertThat(results).allMatch(result -> result.getScore() >= 1);
    }

    @Test
    @DisplayName("매칭된 사용자가 20명 이상인 경우에는 추가 검증 로직이 동작한다")
    void findMatchingV2() throws Exception {
        // Given: 기준 사용자 설정 -> setUp 에서 실행
        // 25명 후보 시뮬레이션
        for (long i = 2; i <= 26; i++) {
            MatchingJpaEntity entity = MatchingJpaEntity.builder()
                    .memberId(i)
                    .category(Category.STUDY)  // 카테고리 동일
                    .mostActiveTime(MostActiveTime.MORNING) // 일치 -> 무조건 1점은 얻을 수 있음
                    .level(i % 5 == 0 ? Level.SEED : Level.FRUITFUL) // 일부 일치
                    .age(i % 2 == 0 ? Age.TWENTIES : Age.THIRTIES) // 나이 선택하지 않았을 경우에 점수에 포함되지 않음
                    .isAttending(i % 5 == 0)
                    .status(MatchingStatus.ACTIVE)
                    .build();
            em.persist(entity);
        }
        em.flush();  // DB 반영

        Thread.sleep(1000); // 별도 스레드에서 전부 실행될 시간을 기다림

        // When
        List<MatchingResult> results = matchingQueryRepository.findMatchingUsers(baseDto);

        // Then: 상위 20명만 반환, 정렬 기준 확인
        assertThat(results).hasSize(20);

        // 정렬 검증: 첫 번째는 최고 신뢰도 + 구독 true (ID 2: 99.0, 구독 true)
        assertThat(results.getFirst().getMemberId()).isEqualTo(2L);

        // 두 번째: ID 3 (99.0, 구독 false) – ID 2 보다 뒤에 위치하는가
        assertThat(results.get(1).getMemberId()).isEqualTo(3L);

        // 전체 정렬 순서 확인 (신뢰도 내림차순, 같은 점수 시 구독 우선)
        for (int i = 0; i < 19; i++) {
            MatchingResult current = results.get(i);
            MatchingResult next = results.get(i + 1);

            // 신뢰도 비교 (또는 같으면 구독 비교)
            double currentTrust = doubleRedisTemplate.opsForValue().get("member:trust:score:" + current.getMemberId());
            double nextTrust = doubleRedisTemplate.opsForValue().get("member:trust:score:" + next.getMemberId());

            boolean currentSub = booleanRedisTemplate.opsForValue().get("member:subscription:" + current.getMemberId());
            boolean nextSub = booleanRedisTemplate.opsForValue().get("member:subscription:" + next.getMemberId());

            if (currentTrust == nextTrust) {  // 신뢰도 동일
                // boolean 비교: true를 1, false를 0으로 변환하여 >= 확인 (true >= false)
                assertThat(currentSub ? 1 : 0).isGreaterThanOrEqualTo(nextSub ? 1 : 0);
            } else {
                assertThat(currentTrust).isGreaterThan(nextTrust);  // 내림차순 확인
            }
        }
    }
}
