package com.grow.matching_service.matching.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 알림 전송을 위한 DTO 클래스 (임시)
 */
@Getter
@Builder
public class NotificationRequestDto {

    private Long memberId;
    private String content;
    private String notificationType; // 알림 타입 MATCH
    private LocalDateTime timestamp;
}
