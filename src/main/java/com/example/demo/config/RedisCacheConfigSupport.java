package com.example.demo.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@CacheConfig(cacheManager = "redisCacheManager")
public class RedisCacheConfigSupport extends CachingConfigurerSupport {

    private static final Logger logger = LoggerFactory.getLogger(RedisCacheConfigSupport.class);

    private static final String KEY_SEPARATOR = "#";

    @Override
    public CacheErrorHandler errorHandler() {

        return new CacheErrorHandler() {

            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                logger.error("Failure getting from cache: {}, exception: {} ", cache.getName(), exception.getLocalizedMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                logger.error("Failure putting into cache: {}, exception: {} ", cache.getName(), exception.getLocalizedMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                logger.error("Failure evicting from cache: {}, exception: {} ", cache.getName(), exception.getLocalizedMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                logger.error("Failure clearing cache: {}, exception: {} ", cache.getName(), exception.getLocalizedMessage());
            }
        };
    }

    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder keyBuilder = new StringBuilder();
            keyBuilder.append(target.getClass().getSimpleName());
            keyBuilder.append(KEY_SEPARATOR);
            keyBuilder.append(method.getName());
            keyBuilder.append(KEY_SEPARATOR);
            for (Object param : params) {
                keyBuilder.append(param.toString());
                keyBuilder.append(KEY_SEPARATOR);
            }

            return StringUtils.removeEnd(keyBuilder.toString(), KEY_SEPARATOR);
        };
    }

}