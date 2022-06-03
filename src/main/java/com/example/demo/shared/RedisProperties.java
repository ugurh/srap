package com.example.demo.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RedisProperties {

    private String host;
    private int port;
    private int database;
    private String password;
    private long defaultTTL;
    private long timeout;
    private Map<String, String> cachesTTL;
}
