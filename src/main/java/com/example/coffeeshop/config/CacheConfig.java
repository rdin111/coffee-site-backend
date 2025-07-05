package com.example.coffeeshop.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import java.time.Duration;


@Configuration
public class CacheConfig {

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        // This configuration tells Spring how to serialize/deserialize objects for caching
        return RedisCacheConfiguration.defaultCacheConfig()
                // Set a default Time-To-Live (TTL) for cache entries, e.g., 10 minutes
                .entryTtl(Duration.ofMinutes(10))
                // Do not cache null values
                .disableCachingNullValues()
                // Define how to serialize the cache values
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }
}
