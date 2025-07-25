package com.grow.matching_service.matching.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 알림 전송을 위한 DTO 클래스.
 * 이 DTO는 알림 요청 데이터를 캡슐화하며, 멤버 ID, 내용, 타입, 타임스탬프, 재시도 횟수를 포함합니다.
 * 주로 NotificationServiceClient나 QueueService에서 사용되어 장애 시 재시도 로직을 지원합니다.
 *
 * <p>이 클래스는 Lombok의 @Getter와 @Builder를 사용하여 간편한 객체 생성과 접근을 제공합니다.
 * retryCount 필드는 알림 전송 실패 시 증가되며, 재시도 한계를 관리할 수 있습니다.</p>
 *
 * @see java.time.LocalDateTime
 */
@Getter
public class NotificationRequestDto {

    /**
     * 알림을 받을 멤버의 고유 ID.
     */
    private final Long memberId;

    /**
     * 알림 내용 (텍스트 메시지).
     */
    private final String content;

    /**
     * 알림 타입 (예: "MATCH_SUCCESS" 등).
     */
    private final String notificationType;

    /**
     * 알림 생성 타임스탬프.
     */
    private final LocalDateTime timestamp;

    /**
     * 재시도 횟수 (초기값 0, 실패 시 증가).
     */
    private int retryCount;

    /**
     * NotificationRequestDto의 빌더 생성자.
     * 필수 필드를 초기화하며, retryCount는 기본값 0으로 설정됩니다.
     *
     * @param memberId 알림 대상 멤버 ID
     * @param content 알림 내용
     * @param notificationType 알림 타입 (예: "MATCH_SUCCESS")
     * @param timestamp 알림 타임스탬프
     */
    @Builder
    public NotificationRequestDto(Long memberId,
                                  String content,
                                  String notificationType,
                                  LocalDateTime timestamp) {
        this.memberId = memberId;
        this.content = content;
        this.notificationType = notificationType;
        this.timestamp = timestamp;
        this.retryCount = 0;  // 초기 재시도 횟수 설정
    }

    /**
     * 재시도 횟수를 증가시킵니다.
     * 알림 전송 실패 시 호출되어 retryCount를 1 증가시킵니다.
     * 이는 \큐 기반 재시도 로직에서 사용됩니다.
     */
    public void increaseRetryCount() {
        this.retryCount++;  // 재시도 횟수 증가
    }
}
