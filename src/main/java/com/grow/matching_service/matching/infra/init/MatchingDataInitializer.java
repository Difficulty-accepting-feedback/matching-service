package com.grow.matching_service.matching.infra.init;

import com.grow.matching_service.matching.domain.enums.Age;
import com.grow.matching_service.matching.domain.enums.Category;
import com.grow.matching_service.matching.domain.enums.Level;
import com.grow.matching_service.matching.domain.enums.MostActiveTime;
import com.grow.matching_service.matching.domain.model.Matching;
import com.grow.matching_service.matching.domain.repository.MatchingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import java.util.List;

@Slf4j
// @Component
@RequiredArgsConstructor
public class MatchingDataInitializer implements CommandLineRunner {

    private final MatchingRepository matchingRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("[MatchingDataInitializer] 초기 매칭 데이터 생성 시작");
        // 1. memberId=1: STUDY, TEENS, SEED, MORNING, attending=true
        Matching matching1 = Matching.createNew(
                1L,
                Category.STUDY,
                MostActiveTime.MORNING,
                Level.SEED,
                Age.TEENS,
                true,
                "초보자를 위한 기본 스터디 그룹을 원해요. 함께 배우며 성장해요!",
                List.of()
        );
        matchingRepository.save(matching1);

        // 2. memberId=2: HOBBY, TWENTIES, SEEDLING, AFTERNOON, attending=false
        Matching matching2 = Matching.createNew(
                2L,
                Category.HOBBY,
                MostActiveTime.AFTERNOON,
                Level.SEEDLING,
                Age.TWENTIES,
                false,
                "취미 활동을 즐기는 20대 모임. 그림 그리기와 산책을 중심으로!",
                List.of()
        );
        matchingRepository.save(matching2);

        // 3. memberId=3: MENTORING, THIRTIES, SAPLING, EVENING, attending=true
        Matching matching3 = Matching.createNew(
                3L,
                Category.HOBBY,
                MostActiveTime.EVENING,
                Level.SAPLING,
                Age.THIRTIES,
                true,
                "30대 멘토링 세션. 커리어 조언과 스킬 공유를 합니다.",
                List.of()
        );
        matchingRepository.save(matching3);

        // 4. memberId=4: STUDY, FORTIES, BLOOMING, DAWN, attending=true
        Matching matching4 = Matching.createNew(
                4L,
                Category.STUDY,
                MostActiveTime.DAWN,
                Level.BLOOMING,
                Age.FORTIES,
                true,
                "고급 주제 스터디: 40대 전문가 그룹. 새벽 시간에 집중 학습.",
                List.of()
        );
        matchingRepository.save(matching4);

        // 5. memberId=5: HOBBY, FIFTIES, FRUITFUL, MORNING, attending=false
        Matching matching5 = Matching.createNew(
                5L,
                Category.HOBBY,
                MostActiveTime.MORNING,
                Level.FRUITFUL,
                Age.FIFTIES,
                false,
                "50대 전문 취미 활동: 정원 가꾸기와 요가. 여유로운 모닝 타임.",
                List.of()
        );
        matchingRepository.save(matching5);

        log.info("[MatchingDataInitializer] 초기 매칭 데이터 생성 완료: 5개 매칭 저장");
    }
}
