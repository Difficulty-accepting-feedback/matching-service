package com.grow.matching_service.matching.application.service.queue;

import com.grow.matching_service.matching.application.dto.NotificationRequestDto;

public interface QueueService {
    void enqueueNotification(NotificationRequestDto request);
    NotificationRequestDto dequeueNotification();
}
