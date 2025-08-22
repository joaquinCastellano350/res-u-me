package com.joaquin.backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtService {
    private final SecretKey secretKey;
    private final long expirationSeconds;

    public JwtService(
            @Value("${security.jwt.secret}") String secretKey,
            @Value("${security.jwt.expiration}") long expirationSeconds
    ){
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(UUID userId, String email){
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(expirationSeconds);

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .claim("email", email)
                .signWith(secretKey)
                .compact();
    }

    public UUID parseUserId(String token){
        String sub = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody().
                getSubject();

        return UUID.fromString(sub);
    }


}
