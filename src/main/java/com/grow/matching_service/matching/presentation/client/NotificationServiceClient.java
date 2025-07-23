package com.grow.matching_service.matching.presentation.client;

import com.grow.matching_service.matching.application.dto.NotificationRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @FeignClient 클라이언트 이름과 URL을 지정
 * 1. NotificationServiceClient 를 @Autowired
 * 2. sendNotification 메서드 호출하면서 dto 를 전송하면
 * 3. 자동으로 notification-service 에게 요청을 보낸다
 */
@FeignClient(name = "notification-service",
        url = "${notification.service.url}")
public interface NotificationServiceClient {

    @PostMapping("/notifications")
    void sendNotification(@RequestBody NotificationRequestDto request);
}
