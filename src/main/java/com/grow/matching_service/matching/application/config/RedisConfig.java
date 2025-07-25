package com.grow.matching_service.matching.application.config;

import com.grow.matching_service.matching.application.dto.NotificationRequestDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, NotificationRequestDto> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, NotificationRequestDto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 키 직렬화 설정 (직렬화/역직렬화 모두 처리)
        template.setKeySerializer(new StringRedisSerializer());
        // 값 직렬화 설정 (직렬화/역직렬화 모두 처리, 필요 시 JSON 직렬화기로 변경)
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }
}
