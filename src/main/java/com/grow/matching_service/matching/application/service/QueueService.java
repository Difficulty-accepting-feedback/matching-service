package com.grow.matching_service.matching.application.service;

import com.grow.matching_service.matching.application.dto.NotificationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Redis를 사용해 오류로 인해 전송되지 못한 알림 메시지를 저장하는 서비스 클래스.
 * Queue 구조를 이용하여 FIFO(First-In-First-Out) 방식으로 요청을 관리합니다.
 *
 * <p>이 서비스는 알림 전송 실패 시 fallback으로 요청을 Redis 리스트에 저장하고,
 * 나중에 dequeue하여 재전송할 수 있도록 지원합니다. Spring의 {@link RedisTemplate}을 활용하며,
 * 비동기 처리({@link Async})를 통해 효율성을 높입니다.</p>
 *
 * <p>의존성:
 * <ul>
 *     <li>{@link RedisTemplate}: Redis 리스트 조작을 위한 템플릿</li>
 * </ul>
 * </p>
 *
 * @see RedisTemplate
 * @see org.springframework.scheduling.annotation.Async
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

    private final RedisTemplate<String, NotificationRequestDto> redisTemplate;
    private static final String QUEUE_KEY = "notification-queue"; // 추후 분리를 할지 고려해 보겠음...

    /**
     * 알림 요청을 Redis 큐에 비동기적으로 추가하는 메서드.
     *
     * <p>이 메서드는 {@link Async} 어노테이션에 의해 비동기 스레드에서 실행됩니다.
     * Redis 리스트의 왼쪽에 요청을 푸시(LPUSH)하여 FIFO 순서를 유지합니다.
     * 성공 시 정보 로그를 남기고, 실패 시 에러 로그를 기록합니다.</p>
     *
     * <p>동작 순서:
     * <ol>
     *     <li>RedisTemplate의 opsForList().leftPush()를 호출하여 큐에 추가합니다.</li>
     *     <li>예외 발생 시 catch 블록에서 로그만 처리합니다.</li>
     * </ol>
     * </p>
     *
     * @param request 큐에 추가할 알림 요청 DTO (null 불가)
     * @see org.springframework.scheduling.annotation.Async
     * @see RedisTemplate#opsForList()
     */
    @Async // 비동기로 메시지를 저장
    public void enqueueNotification(NotificationRequestDto request) {
        try {
            redisTemplate.opsForList().leftPush(QUEUE_KEY, request);
            log.info("알림 요청을 Redis 큐에 추가: {}", request.getContent());
        } catch (Exception e) {
            log.error("Redis 큐 추가 실패: {}", e.getMessage());
        }
    }

    /**
     * Redis 큐에서 알림 요청을 꺼내는 메서드.
     *
     * <p>이 메서드는 Redis 리스트의 오른쪽에서 팝(RPOP)하여 가장 오래된 요청을 반환합니다.
     * 요청이 존재할 경우 정보 로그를 남기고, 실패 시 에러 로그를 기록하며 null을 반환합니다.
     * 이는 재전송 로직이나 스케줄링에서 사용되며, 별도 트랜잭션이 적용되지 않습니다.</p>
     *
     * <p>동작 순서:
     * <ol>
     *     <li>RedisTemplate의 opsForList().rightPop()를 호출하여 큐에서 꺼냅니다.</li>
     *     <li>요청이 null이 아니면 로그를 남깁니다.</li>
     *     <li>예외 발생 시 null을 반환합니다.</li>
     * </ol>
     * </p>
     *
     *
     * @return dequeue된 알림 요청 DTO, 큐가 비어 있거나 실패 시 null
     * @see RedisTemplate#opsForList()
     */
    // 큐에서 항목을 꺼내는 메서드
    public NotificationRequestDto dequeueNotification() {
        try {
            NotificationRequestDto request = redisTemplate.opsForList().rightPop(QUEUE_KEY);
            if (request != null) {
                log.info("Redis 큐에서 알림 요청 꺼냄: {}", request.getContent());
            }
            return request;
        } catch (Exception e) {
            log.error("Redis 큐에서 제거 실패: {}", e.getMessage());
            return null;
        }
    }
}
