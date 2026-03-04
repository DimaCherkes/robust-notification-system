package com.dmytrocherkes.subscriptionservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        this.secretKey = getKey(secretKey);
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public String getUserEmail(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public String getUsername(String token) {
        return getAllClaimsFromToken(token).get(AuthenticationConstants.USERNAME, String.class);
    }

    public Long getUserId(String token) {
        Object userId = getAllClaimsFromToken(token).get(AuthenticationConstants.USER_ID);
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return (Long) userId;
    }

    public List<String> getRoles(String token) {
        return getAllClaimsFromToken(token).get(AuthenticationConstants.ROLE, List.class);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getKey(String secretKey64) {
        byte[] decode64 = Decoders.BASE64.decode(secretKey64);
        return Keys.hmacShaKeyFor(decode64);
    }
}
