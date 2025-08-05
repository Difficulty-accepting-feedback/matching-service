package com.grow.matching_service.matching.application.service;

import com.grow.matching_service.matching.domain.dto.MatchingUpdateRequest;
import com.grow.matching_service.matching.presentation.dto.MatchingRequest;
import com.grow.matching_service.matching.domain.enums.Age;
import com.grow.matching_service.matching.domain.enums.Category;
import com.grow.matching_service.matching.domain.enums.Level;
import com.grow.matching_service.matching.domain.enums.MostActiveTime;
import com.grow.matching_service.matching.domain.model.Matching;
import com.grow.matching_service.matching.domain.repository.MatchingRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@Transactional // 테스트 시 롤백을 위해 사용
class MatchingServiceImplTest {

    @Autowired
    private MatchingServiceImpl matchingService;

    @Autowired
    private MatchingRepository matchingRepository;

    @Test
    @DisplayName("도메인 생성 및 DB 저장 테스트")
    void createMatching() throws Exception {
        //given
        MatchingRequest request = new MatchingRequest(
                1L,
                Category.STUDY,
                MostActiveTime.EVENING,
                Level.BLOOMING,
                Age.TWENTIES,
                true,
                "[TEST1]안녕하세요"
        );

        //when
        matchingService.createMatching(request, 1L);

        //then
        assertThat(matchingRepository.findByMemberId(1L).getFirst().getIntroduction())
                .isEqualTo("[TEST1]안녕하세요");

        Matching savedMatching = matchingRepository.findByMemberId(1L).getFirst();
        log.info("테스트 매칭 소개: {}", savedMatching.getIntroduction());
    }

    @Test
    void updateMatching_successfully_updatesFields() {
        // given: DB에 초기 매칭 데이터 삽입
        Matching initialMatching = Matching.createNew(
                10L,
                Category.HOBBY,
                MostActiveTime.MORNING,
                Level.SEED,
                Age.TEENS,
                true,
                "기존 소개글입니다.",
                List.of()
        );

        Matching savedMatching = matchingRepository.save(initialMatching);
        Long matchingId = savedMatching.getMatchingId();

        MatchingUpdateRequest request = new MatchingUpdateRequest(
                MostActiveTime.EVENING,
                Level.BLOOMING,
                Age.TWENTIES,
                false,
                "수정된 소개글입니다."
        );

        // when: 서비스 호출
        matchingService.updateMatching(matchingId, request);

        // then: DB에서 조회해 변경 확인
        Optional<Matching> updatedMatchingOpt = matchingRepository.findByMatchingId(matchingId);
        assertTrue(updatedMatchingOpt.isPresent());
        Matching updatedMatching = updatedMatchingOpt.get();
        assertEquals(MostActiveTime.EVENING, updatedMatching.getMostActiveTime());
        assertEquals(Level.BLOOMING, updatedMatching.getLevel());
        assertEquals(Age.TWENTIES, updatedMatching.getAge());
        assertEquals(false, updatedMatching.getIsAttending());
        assertEquals("수정된 소개글입니다.", updatedMatching.getIntroduction());
    }
}