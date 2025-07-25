package com.grow.matching_service.matching.presentation.client;

import com.grow.matching_service.matching.application.dto.NotificationRequestDto;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 알림 서비스(notification-service)와 통신하기 위한 Feign 클라이언트 인터페이스.
 * 이 인터페이스는 외부 알림 서비스로 알림 요청을 전송하며, Spring Cloud OpenFeign을 기반으로 합니다.
 *
 * <p>주요 설정:</p>
 * <ul>
 *   <li>name: "notification-service" - 서버 이름 지정.</li>
 *   <li>url: "${notification.service.url}" - 프로퍼티에서 동적으로 URL 설정 (application.yml).</li>
 *   <li>fallback: NotificationFallbackFactory.class - 장애 시 fallback 로직 실행 (큐 저장).</li>
 *   <li>@Retry: "notificationRetry" - 재시도 정책 적용 (Resilience4j).</li>
 *   <li>@CircuitBreaker: "notificationCircuitBreaker" - 서킷 브레이커 적용 (장애 시 호출 차단).</li>
 * </ul>
 *
 * <p>사용 방법:</p>
 * <ol>
 *   <li>이 인터페이스를 @Autowired로 주입합니다.</li>
 *   <li>sendNotification 메서드를 호출하여 NotificationRequestDto를 전송합니다.</li>
 *   <li>자동으로 notification-service의 /notifications 엔드포인트로 POST 요청을 보냅니다.</li>
 * </ol>
 *
 * @see org.springframework.cloud.openfeign.FeignClient
 * @see io.github.resilience4j.retry.annotation.Retry
 * @see io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
 * @see NotificationFallbackFactory  // fallback 구현 클래스
 * @see NotificationRequestDto  // 요청 DTO 클래스
 */
@FeignClient(name = "notification-service",
        url = "${notification.service.url}",
        fallback = NotificationFallbackFactory.class) // fallback 클래스 지정
@Retry(name = "notificationRetry")  // 재시도 정책 설정 (Resilience4j)
@CircuitBreaker(name = "notificationCircuitBreaker")  // circuit breaker 설정 (Resilience4j)
public interface NotificationServiceClient {

    /**
     * 알림을 외부 서비스로 전송합니다.
     * notification-service의 /notifications 엔드포인트로 POST 요청을 보내 알림을 처리합니다.
     *
     * <p>장애 발생 시 (예: 타임아웃, 5xx 오류) fallbackFactory가 호출되어 큐에 저장됩니다.</p>
     *
     * @param request 알림 요청 데이터 (NotificationRequestDto 객체)
     * @throws FeignException Feign 클라이언트 오류 발생 시 (예: HTTP 오류)
     * @throws CallNotPermittedException Circuit Breaker가 OPEN 상태일 때
     */
    @PostMapping("/notifications")
    void sendNotification(@RequestBody NotificationRequestDto request);
}