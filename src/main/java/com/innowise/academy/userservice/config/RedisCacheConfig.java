package com.innowise.academy.userservice.config;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

@EnableCaching
@CacheConfig
public class RedisCacheConfig {
    @Bean
    public RedisCacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper) {

        RedisSerializer<Object> serializer =
                new JacksonJsonRedisSerializer<>(
                        objectMapper,
                        Object.class
                );

        RedisCacheConfiguration configuration =
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(serializer)
                        )
                        .entryTtl(Duration.ofMinutes(10))
                        .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(configuration)
                .build();
    }
}
