package com.grow.matching_service.matching.application.service;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Slf4j
@SpringBootTest
@ActiveProfiles("test")
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
                6L,
                Category.STUDY,
                MostActiveTime.EVENING,
                Level.BLOOMING,
                Age.TWENTIES,
                true,
                "[TEST1]안녕하세요"
        );

        //when
        matchingService.createMatching(request);

        //then
        assertThat(matchingRepository.findByMemberId(6L).getFirst().getIntroduction())
                .isEqualTo("[TEST1]안녕하세요");

        Matching savedMatching = matchingRepository.findByMemberId(1L).getFirst();
        log.info("테스트 매칭 소개: {}", savedMatching.getIntroduction());
    }
}