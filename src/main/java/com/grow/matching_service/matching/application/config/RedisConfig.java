package com.grow.matching_service.matching.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.grow.matching_service.matching.application.dto.NotificationRequestDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, NotificationRequestDto> dtoRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, NotificationRequestDto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 키 직렬화 설정 (직렬화/역직렬화 모두 처리)
        template.setKeySerializer(new StringRedisSerializer());

        // 값 직렬화 설정
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // 타입 정보를 포함하여 직렬화 (기본 설정은 NON_FINAL)
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        Jackson2JsonRedisSerializer<NotificationRequestDto> serializer = new Jackson2JsonRedisSerializer<>(
                objectMapper,
                NotificationRequestDto.class
        );
        template.setValueSerializer(serializer);

        template.afterPropertiesSet(); // 설정 적용

        return template;
    }

    @Bean
    public RedisTemplate<String, String> customStringRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer()); // String 값용

        template.afterPropertiesSet();
        return template;
    }
}
