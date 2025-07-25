package com.grow.matching_service.matching.presentation.client;

import com.grow.matching_service.matching.application.service.QueueService;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeoutException;

/**
 * NotificationServiceClient의 FallbackFactory 구현 클래스.
 * 이 클래스는 NotificationServiceClient가 실패할 경우 대체 로직을 제공합니다.
 * 주로 Circuit Breaker, Feign 예외, 타임아웃 등의 장애 상황에서 알림 요청을 큐에 저장하여 후속 처리를 보장합니다.
 *
 * <p>이 구현은 Spring Cloud Circuit Breaker (Resilience4j)와 Feign 클라이언트를 기반으로 하며,
 * 장애 발생 시 로그를 기록하고 요청을 QueueService로 전달합니다.</p>
 *
 * @see io.github.resilience4j.circuitbreaker.CallNotPermittedException
 * @see feign.FeignException
 * @see java.util.concurrent.TimeoutException
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationFallbackFactory implements FallbackFactory<NotificationServiceClient> {

    private final QueueService queueService;

    /**
     * TODO 장애 대응을 위해 메트릭 서비스 추가할 것 (모니터링 용도)
     * 예: Prometheus나 Micrometer를 사용하여 실패 횟수, 지연 시간 등을 모니터링.
     */

    /**
     * Fallback 인스턴스를 생성합니다.
     * Throwable cause에 따라 적절한 예외 처리를 수행한 후, 요청을 큐에 저장합니다.
     *
     * @param cause 발생한 예외 (Circuit Breaker, Feign, Timeout 등)
     * @return NotificationServiceClient의 대체 구현 (람다 표현식으로 요청 처리)
     */
    @Override
    public NotificationServiceClient create(Throwable cause) {
        return request -> {
            switch (cause) {
                case CallNotPermittedException callNotPermittedException ->
                        handleExceptions("Circuit Breaker OPEN 상태: {}", cause);  // Circuit Breaker가 열린 상태로 인해 호출 불가
                case FeignException feignEx -> handleFeignException(feignEx);  // Feign 클라이언트 예외 처리 (Retry 실패 등)
                case TimeoutException timeoutException ->   // 타임아웃 예외
                        handleExceptions("타임아웃 오류: {}", cause);  // 요청 시간이 초과된 경우
                default -> handleExceptions("기타 오류: {}", cause);  // 예상치 못한 다른 예외
            }

            queueService.enqueueNotification(request);  // 별도 큐 서비스로 저장하여 후속 처리 보장
            log.info("알림을 큐에 저장: 후속 처리 예정");
        };
    }

    /**
     * FeignException을 처리합니다.
     * HTTP 상태 코드를 로그로 기록하며, 503 (Service Unavailable)인 경우 특별 경고를 추가합니다.
     *
     * @param feignEx 발생한 FeignException 인스턴스
     */
    private void handleFeignException(FeignException feignEx) {
        log.error("Feign 오류 (Retry 실패): HTTP {} - {}",
                feignEx.status(), feignEx.getMessage());
        // HTTP 503 (Service Unavailable)인 경우 특별 처리
        if (feignEx.status() == 503) {
            log.warn("서비스 이용 불가: 복구 대기 중");
        }
    }

    /**
     * 일반 예외를 처리합니다.
     * 주어진 메시지 형식으로 로그를 기록합니다.
     *
     * @param s 로그 메시지 형식 문자열 (예: "타임아웃 오류: {}")
     * @param cause 발생한 Throwable 인스턴스
     */
    private void handleExceptions(String s, Throwable cause) {
        log.error(s, cause.getMessage());
    }
}