package com.grow.matching_service.matching.application.event;

import com.grow.matching_service.matching.application.dto.NotificationRequestDto;
import com.grow.matching_service.matching.infra.dto.MatchingQueryDto;
import com.grow.matching_service.matching.infra.dto.MatchingResult;
import com.grow.matching_service.matching.domain.dto.event.MatchingSavedEvent;
import com.grow.matching_service.matching.infra.repository.MatchingQueryRepository;
import com.grow.matching_service.matching.presentation.client.NotificationServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 매칭 이벤트 핸들러 클래스.
 * <p>
 * 이 클래스는 매칭 저장 이벤트({@link MatchingSavedEvent})를 처리하여,
 * 매칭된 사용자들을 조회하고 알림을 전송합니다. Spring의 이벤트 리스너 메커니즘을 활용합니다.
 *
 * @author sun
 * @since 1.0
 * @see MatchingSavedEvent
 * @see MatchingQueryRepository
 * @see NotificationServiceClient
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingEventHandler {

    /**
     * 매칭 쿼리 리포지토리.
     * <p>
     * 매칭 조건에 맞는 사용자 목록을 조회하는 데 사용됩니다.
     */
    private final MatchingQueryRepository queryRepository;
    /**
     * 알림 서비스 클라이언트.
     * <p>
     * 매칭 성공 시 사용자에게 알림을 전송하는 Feign 클라이언트입니다.
     */
    private final NotificationServiceClient notificationService;

    /**
     * 매칭 저장 이벤트를 비동기적으로 처리합니다.
     * <p>
     * 이벤트에서 받은 {@link MatchingQueryDto}를 기반으로 매칭 사용자 목록을 조회하고,
     * 본인과 상대방에게 각각 알림을 전송합니다. 매칭 대상이 없을 경우 로그를 남기고 종료합니다.
     * <p>
     * 이 메서드는 Spring의 {@link EventListener}를 통해 이벤트 구독을 처리하며,
     * {@link Async}로 비동기 실행됩니다.
     *
     * @param event 매칭 저장 이벤트 객체 ({@link MatchingSavedEvent})
     * @see MatchingSavedEvent
     * @see MatchingQueryRepository#findMatchingUsers(MatchingQueryDto)
     */
    @Async
    @EventListener
    public void handleMatchingSaved(MatchingSavedEvent event) {
        MatchingQueryDto reference = event.getDto();
        List<MatchingResult> matchingUsers = queryRepository.findMatchingUsers(reference);

        // 빈 리스트 추출 시 예외 처리
        if (matchingUsers.isEmpty()) {
            log.info("[MATCH] 매칭 대상이 없습니다. memberId: {}", reference.getMemberId());
            return;
        }

        log.info("[MATCH] 매칭 대상이 있습니다. memberId: {}, 인원 수: {}",
                reference.getMemberId(), matchingUsers.size());

        sendNotificationOwn(reference, matchingUsers);

        for (MatchingResult matchingUser : matchingUsers) {
            logging(matchingUser, reference);
            sendNotificationOthers(matchingUser, reference);
        }
    }

    /**
     * 매칭 성공 시 본인에게 알림을 전송합니다.
     * <p>
     * 매칭된 사용자 수를 요약하여 "MATCH_SUCCESS" 타입의 알림을 보냅니다.
     *
     * @param reference 본인 매칭 쿼리 DTO ({@link MatchingQueryDto})
     * @param matchingUsers 매칭된 사용자 목록 ({@link List}<{@link MatchingResult}>)
     * @see NotificationServiceClient#sendNotification(NotificationRequestDto)
     */
    private void sendNotificationOwn(MatchingQueryDto reference,
                                     List<MatchingResult> matchingUsers) {
        // 매칭 성공 시 본인에게 알림 전송 (매칭 목록 요약)
        notificationService.sendNotification(NotificationRequestDto.builder()
                .memberId(reference.getMemberId())
                .content("매칭 성공! " + matchingUsers.size() + "명의 사용자와 매칭되었습니다.")
                .notificationType("MATCH_SUCCESS")
                .build());
    }

    /**
     * 매칭 성공 시 각 상대방에게 알림을 전송합니다.
     * <p>
     * 본인 ID와 유사도 점수를 포함한 "MATCH_SUCCESS" 타입의 알림을 보냅니다.
     *
     * @param matchingUser 매칭된 상대방 결과 DTO ({@link MatchingResult})
     * @param reference 본인 매칭 쿼리 DTO ({@link MatchingQueryDto})
     * @see NotificationServiceClient#sendNotification(NotificationRequestDto)
     */
    private void sendNotificationOthers(MatchingResult matchingUser,
                                        MatchingQueryDto reference) {
        notificationService.sendNotification(NotificationRequestDto.builder()
                .memberId(matchingUser.getMemberId())
                .content("새로운 매칭! 사용자 " + reference.getMemberId() + "와 매칭되었습니다. " +
                        "유사도: " + matchingUser.getScore() + "점")
                .notificationType("MATCH_SUCCESS")
                .build());
    }

    /**
     * 매칭 결과를 로그로 기록합니다.
     * <p>
     * 본인 ID, 상대방 ID, 유사도 점수를 INFO 레벨로 로그합니다.
     *
     * @param matchingUser 매칭된 상대방 결과 DTO ({@link MatchingResult})
     * @param reference 본인 매칭 쿼리 DTO ({@link MatchingQueryDto})
     */
    private void logging(MatchingResult matchingUser, MatchingQueryDto reference) {
        log.info("[MATCH] 현재 사용자 {} 와 유사한 조건의 사용자 {} - 유사도: {}점",
                reference.getMemberId(),
                matchingUser.getMemberId(),
                matchingUser.getScore()
        );
    }
}
