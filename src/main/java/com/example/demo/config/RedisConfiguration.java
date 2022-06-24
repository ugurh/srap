package com.example.demo.config;

import com.example.demo.shared.RedisProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableCaching
public class RedisConfiguration {
    private static final int CONNECT_TIMEOUT = 1000;
    private static final int COMMAND_TIMEOUT = 60000;
    private final RedisProperties redisProperties;

    public RedisConfiguration(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Bean
    public RedisTemplate<String, Serializable> redisCacheTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Serializable> template = new RedisTemplate<>();
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        template.setKeySerializer(jackson2JsonRedisSerializer);
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        template.afterPropertiesSet();
        return template;
    }

    @Bean("redisCacheManager")
    public RedisCacheManager redisCacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        RedisCacheConfiguration redisCacheConfiguration = config
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        if (Objects.nonNull(redisProperties.getCachesTTL())) {
            for (Map.Entry<String, String> cacheNameAndTimeout : redisProperties.getCachesTTL().entrySet()) {
                cacheConfigurations.put(cacheNameAndTimeout.getKey(), createCacheConfiguration(redisCacheConfiguration, Long.parseLong(cacheNameAndTimeout.getValue())));
            }
        }

        RedisCacheConfiguration cacheDefaults = createCacheConfiguration(redisCacheConfiguration, redisProperties.getDefaultTTL());

        return RedisCacheManager
                .builder(factory)
                .cacheDefaults(cacheDefaults)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
        redisStandaloneConfiguration.setPassword(RedisPassword.of(redisProperties.getPassword()));

        final SocketOptions socketOptions = SocketOptions.builder()
                .connectTimeout(Duration.ofMillis(CONNECT_TIMEOUT)).build();

        final ClientOptions clientOptions = ClientOptions.builder()
                .socketOptions(socketOptions)
                .build();

        final LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(COMMAND_TIMEOUT))
                .clientOptions(clientOptions)
                .build();

        final LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
        connectionFactory.setValidateConnection(true);

        return connectionFactory;
    }

    private RedisCacheConfiguration createCacheConfiguration(RedisCacheConfiguration redisCacheConfiguration, long timeoutInSeconds) {
        return redisCacheConfiguration
                .entryTtl(Duration.ofSeconds(timeoutInSeconds));
    }

}
