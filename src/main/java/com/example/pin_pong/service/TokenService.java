package com.example.pin_pong.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    @PostConstruct
    protected void init() {
        key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }

    public String createToken(Long memberId) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(now)
                .setExpiration(new Date(nowMillis + 3600000)) // 1 hour
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
