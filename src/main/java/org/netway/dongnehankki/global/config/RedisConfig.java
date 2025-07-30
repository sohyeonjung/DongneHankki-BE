package org.netway.dongnehankki.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(basePackages = "org.netway.dongnehankki.global.auth.jwt")
public class RedisConfig {
}
