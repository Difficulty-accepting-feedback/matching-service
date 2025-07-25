package com.grow.matching_service.matching.presentation.client;

import com.grow.matching_service.matching.application.dto.NotificationRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;

class NotificationServiceClientTest {

    @Test
    @DisplayName("sendNotification 메서드 호출 검증")
    void testSendNotification() {
        NotificationServiceClient mockClient = Mockito.mock(NotificationServiceClient.class);
        NotificationRequestDto dto = NotificationRequestDto.builder()
                .memberId(1L)
                .content("테스트 알림")
                .notificationType("MATCH_SUCCESS")
                .build();
        mockClient.sendNotification(dto);
        verify(mockClient).sendNotification(dto);  // 호출 검증
    }
}