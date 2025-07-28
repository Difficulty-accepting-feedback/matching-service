package com.grow.matching_service.matching.application.service.queue;

import com.grow.matching_service.matching.application.dto.NotificationRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class QueueServiceImplTest {

    @Autowired
    private QueueServiceImpl queueService;

    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, NotificationRequestDto> dtoRedisTemplate;

    private static final String QUEUE_KEY = "notification:queue";
    private static final String SET_KEY = "notification:in_queue";
    private NotificationRequestDto dto;

    @BeforeEach
    void setUp() {
        // 테스트 시작 전 키 초기화
        stringRedisTemplate.delete(SET_KEY);
        dtoRedisTemplate.delete(QUEUE_KEY);

        dto = NotificationRequestDto.builder()
                .memberId(1L)
                .content("Test Notification")
                .notificationType("MATCH_SUCCESS")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @AfterEach
    void tearDown() {
        // 테스트 종료 후 키 초기화
        stringRedisTemplate.delete(SET_KEY);
        dtoRedisTemplate.delete(QUEUE_KEY);
    }

    @Test
    @DisplayName("중복 요청을 전송하고, 한 번만 저장이 되는지 확인하는 테스트")
    void testDuplicateEnqueuePrevention() throws Exception {
        // given: 동일한 requestId를 가진 요청 생성
        String requestId = dto.getUuid();
        log.info("Test RequestId: {}", requestId);

        // when: 동일한 요청을 3번 enqueue 시도
        queueService.enqueueNotification(dto);
        queueService.enqueueNotification(dto);
        queueService.enqueueNotification(dto);

        Thread.sleep(1000); // 비동기 처리로 인해 위의 로직이 다 끝나기를 기다려야만 함

        // then: Set과 List에 한 번만 추가되었는지 확인
        Long setSize = stringRedisTemplate.opsForSet().size(SET_KEY);
        Long queueSize = dtoRedisTemplate.opsForList().size(QUEUE_KEY);

        assertThat(setSize).isEqualTo(1L);
        assertThat(queueSize).isEqualTo(1L);

        // Dequeue 후 확인
        NotificationRequestDto dequeued = queueService.dequeueNotification();
        assertThat(dequeued).isEqualTo(dto); // equals and hashCode 확인
        assertThat(dtoRedisTemplate.opsForList().size(QUEUE_KEY)).isEqualTo(0L);
        assertThat(stringRedisTemplate.opsForSet().size(SET_KEY)).isEqualTo(0L);
    }

    @Test
    @DisplayName("동시성 테스트 - 별도의 스레드에서 동일한 내용을 함께 입력, 중복으로 입력되지 않는지 확인")
    void testConcurrentEnqueue() throws Exception {
        // given: 여러 스레드에서 enqueue 시도
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when: 10개 스레드에서 enqueue (5개 모두 중복)
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    queueService.enqueueNotification(dto);
                } finally {
                    latch.countDown();
                }
            });
        }

        Thread.sleep(1000); // 비동기 처리로 인해 위의 로직이 다 끝나기를 기다려야만 함

        // then: 중복은 제거되어 Set 크기는 1, Queue도 동일
        Long setSize = stringRedisTemplate.opsForSet().size(SET_KEY);
        Long queueSize = dtoRedisTemplate.opsForList().size(QUEUE_KEY);

        assertThat(setSize).isEqualTo(1L);
        assertThat(queueSize).isEqualTo(1L);
    }

    @Test
    @DisplayName("동시성 테스트 - 별도의 스레드에서 동일한 내용을 함께 꺼내고 중복으로 꺼내지지는 않는지 확인")
    void testConcurrentDequeue() throws InterruptedException {
        // given: enqueue 시도
        queueService.enqueueNotification(dto);

        Thread.sleep(1000);

        // when: 5개 스레드에서 동시에 dequeue 시도
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    queueService.dequeueNotification();
                } finally {
                    latch.countDown();
                }
            });
        }

        Thread.sleep(1000);

        // then: dequeue가 성공적으로 처리되어 Queue와 Set이 비워짐 (중복/누락 없음)
        Long remainingQueueSize = stringRedisTemplate.opsForList().size(QUEUE_KEY);
        Long remainingSetSize = dtoRedisTemplate.opsForSet().size(SET_KEY);

        assertThat(remainingQueueSize).isEqualTo(0L);
        assertThat(remainingSetSize).isEqualTo(0L);
    }
}