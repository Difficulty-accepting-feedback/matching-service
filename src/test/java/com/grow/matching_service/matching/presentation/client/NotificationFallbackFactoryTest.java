package com.grow.matching_service.matching.presentation.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.grow.matching_service.matching.application.dto.NotificationRequestDto;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@WireMockTest
@ActiveProfiles("test")
class NotificationFallbackFactoryTest {

    @Autowired
    private NotificationServiceClient notificationServiceClient;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private WireMockServer wireMockServer;
    private NotificationRequestDto requestDto;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port()); // http://localhost:8080 설정
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("notificationCircuitBreaker");
        circuitBreaker.reset(); // 초기 상태로 초기화

        // 테스트 DTO 준비
        requestDto = NotificationRequestDto.builder()
                .memberId(1L)
                .content("Test Notification")
                .notificationType("MATCH_SUCCESS")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("성공 응답 확인")
    void testSuccessfulNotification() {
        // given: 성공 응답 모킹
        stubFor(post("/notifications")
                .willReturn(aResponse().withStatus(200)));

        // when: 호출
        notificationServiceClient.sendNotification(requestDto);

        // then: 성공 응답 확인
        verify(1, postRequestedFor(urlEqualTo("/notifications")));
    }

    @Test
    @DisplayName("예외 발생 시 retry 가 제대로 동작하는지 테스트")
    void testRetryOnTimeout() {
        // given: 실패의 경우 (maxAttempts: 3)
        stubFor(post("/notifications")
                .willReturn(aResponse()
                        .withFixedDelay(100)  // 지연으로 Timeout 시뮬레이션
                        .withStatus(500)      // 실패 응답
                        .withBody("Timeout Exception")));

        // when & then: Retry 발생 확인 (예외 throw 기대) - 중간 예외가 아니라 최종 예외가 반환
        assertThatThrownBy(() -> notificationServiceClient.sendNotification(requestDto))
                .isInstanceOf(FeignException.class)
                .hasMessageContaining("Timeout Exception");  // 메시지 검증

        // then: 재시도 횟수 확인 (초기 1 + 재시도 2 = 3)
        verify(3, postRequestedFor(urlEqualTo("/notifications")));
    }

    @Test
    @DisplayName("CircuitBreaker가 열린 경우를 테스트")
    void testCircuitBreakerOpen() {
        // given: 연속 실패 후 서킷 브레이커 열린 상태를 가정
        stubFor(post("/notifications")
                .willReturn(aResponse().withStatus(500)));

        // when: 5회 연속 호출로 Open 상태 유발
        for (int i = 0; i < 5; i++) {
            try {
                notificationServiceClient.sendNotification(requestDto);
            } catch (Exception ignored) {
                // 무시 (실패 기대)
            }
        }

        // Then: Circuit Breaker Open 확인, 요청 횟수 확인 (Open 후 호출되지 않음)
        verify(5, postRequestedFor(urlEqualTo("/notifications")));
        assertThatThrownBy(() -> notificationServiceClient.sendNotification(requestDto))
                .isInstanceOf(CallNotPermittedException.class);

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("notificationCircuitBreaker");
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }
}