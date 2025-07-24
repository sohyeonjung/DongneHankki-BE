package org.netway.dongnehankki.global.auth.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash("refreshToken")
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    private Long userId;
    private String token;

    @TimeToLive
    private Long expiration;
}