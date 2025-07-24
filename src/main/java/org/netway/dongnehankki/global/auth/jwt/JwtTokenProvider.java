package org.netway.dongnehankki.global.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.netway.dongnehankki.global.auth.CustomUserDetails;
import org.netway.dongnehankki.user.domain.User.Role;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;

    @Value("${jwt.access-token-expiration-minutes}")
    private long accessTokenExpirationMinutes;

    @Value("${jwt.refresh-token-expiration-minutes}")
    private long refreshTokenExpirationMinutes;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + accessTokenExpirationMinutes * 60 * 1000);

        JwtBuilder builder = Jwts.builder()
                .claim("role", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256);

        if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            builder.claim("userId", customUserDetails.getUser().getUserId());
            if (customUserDetails.getUser().getRole() == Role.OWNER && customUserDetails.getUser().getStore() != null) {
                builder.claim("storeId", customUserDetails.getUser().getStore().getStoreId());
            }
        }

        return builder.compact();
    }

    public String generateRefreshToken(Long userId) {
        long now = (new Date()).getTime();
        Date refreshTokenExpiresIn = new Date(now + refreshTokenExpirationMinutes * 60 * 1000);

        return Jwts.builder()
                .claim("userId", userId)
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("userId", Long.class);
    }

    public long getRefreshTokenExpirationMinutes() {
        return refreshTokenExpirationMinutes;
    }


    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        Collection<? extends GrantedAuthority> authorities = 
                Arrays.stream(claims.get("role").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        String userId = claims.get("userId", Long.class).toString();
        UserDetails principal = new User(userId, "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
