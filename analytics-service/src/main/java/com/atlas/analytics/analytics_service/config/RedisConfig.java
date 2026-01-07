package com.atlas.analytics.analytics_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.atlas.analytics.analytics_service.dto.LocationUpdateDto;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, LocationUpdateDto> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, LocationUpdateDto> template = new RedisTemplate<>();

        // Use string serializer for key
        template.setKeySerializer(new StringRedisSerializer());

        // Use JSON for value
        JacksonJsonRedisSerializer<LocationUpdateDto> serializer = new JacksonJsonRedisSerializer<>(
                LocationUpdateDto.class);
        template.setValueSerializer(serializer);
        template.setConnectionFactory(connectionFactory);
        template.afterPropertiesSet();

        return template;
    }
}
