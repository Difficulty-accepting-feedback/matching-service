package com.grow.matching_service.matching.application.service;

import com.grow.matching_service.matching.application.dto.NotificationRequestDto;
import com.grow.matching_service.matching.application.service.queue.QueueService;
import com.grow.matching_service.matching.presentation.client.NotificationServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 알림 재전송을 위한 스케줄링 서비스 클래스.
 * Redis 큐에 저장된 알림 요청을 주기적으로 dequeue하여 재전송을 시도합니다.
 * 이는 Notification 서비스의 장애 시 fallback으로 저장된 요청을 처리하기 위한 목적으로 사용됩니다.
 *
 * <p>이 서비스는 Spring의 {@link Scheduled} 어노테이션을 사용하여 주기적으로 실행되며,
 * 큐에서 요청을 꺼내 재전송을 시도합니다. 재시도 횟수가 초과된 경우 경고 로그를 남기고 스킵합니다.</p>
 *
 * <p>의존성:
 * <ul>
 *     <li>{@link QueueService}: Redis 큐 관리를 위한 서비스</li>
 *     <li>{@link NotificationServiceClient}: 알림 전송을 위한 Feign 클라이언트</li>
 * </ul>
 * </p>
 *
 * @see QueueService
 * @see NotificationServiceClient
 * @see org.springframework.scheduling.annotation.Scheduled
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationRetryService {

    private final QueueService queueService;
    private final NotificationServiceClient notificationServiceClient;

    /**
     * Redis 큐에 저장된 알림 요청을 10분마다 재전송 시도하는 스케줄링 메서드.
     *
     * <p>이 메서드는 {@link Scheduled} 어노테이션에 의해 fixedRate(고정 간격)로 실행됩니다.
     * 큐에서 요청을 dequeue하여 {@link NotificationServiceClient#sendNotification(NotificationRequestDto)}를 호출합니다.
     * 성공 시 로그를 남기고 루프를 종료합니다. 실패 시 재시도 횟수를 증가시키고 큐에 다시 enqueue 합니다.</p>
     *
     * <p>동작 순서:
     * <ol>
     *     <li>큐가 비어 있을 때까지 while 루프로 dequeue를 반복합니다.</li>
     *     <li>재시도 횟수가 3회 이상이면 경고 로그를 남기고 스킵합니다.</li>
     *     <li>전송 시도 중 예외 발생 시 큐에 다시 추가합니다.</li>
     * </ol>
     * </p>
     *
     * @see org.springframework.scheduling.annotation.Scheduled#fixedRate()
     * @see QueueService#dequeueNotification()
     * @see QueueService#enqueueNotification(NotificationRequestDto)
     * @see NotificationServiceClient#sendNotification(NotificationRequestDto)
     */
    @Scheduled(fixedRate = 60000 * 10) // 10분마다 실행
    public void retryNotifications() {
        NotificationRequestDto request;
        while ((request = queueService.dequeueNotification()) != null) {
            if (checkRetryCount(request)) continue;
            log.info("Redis 큐에서 알림 재전송 중: {}", request.getContent());
            try {
                notificationServiceClient.sendNotification(request);
                log.info("[Notification] Redis 큐에서 알림 재전송 성공: {}", request.getContent());
            } catch (Exception e) {
                log.error("[Notification] 재전송 실패, 큐에 다시 추가");
                request.increaseRetryCount(); // 재시도 횟수 증가
                queueService.enqueueNotification(request); // 재전송 실패시 큐에 다시 추가
            }
        }
    }

    private boolean checkRetryCount(NotificationRequestDto request) {
        if (request.getRetryCount() >= 3) { // 최대 3회 제한
            log.warn("[Notification] 재시도 횟수 초과: {}", request.getContent());
            return true;
        }
        return false;
    }
}
