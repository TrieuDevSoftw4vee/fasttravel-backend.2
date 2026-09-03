package com.fasttravel.util;

import com.fasttravel.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey key;
    private final long expiration;

    public JwtUtil(@Value("${app.jwt.secret}") String s, @Value("${app.jwt.access-expiration}") long e) {
        key = Keys.hmacShaKeyFor(s.getBytes(StandardCharsets.UTF_8));
        expiration = e;
    }

    public String create(User u) {
        return Jwts.builder().subject(u.getEmail()).claim("uid", u.getId()).claim("role", u.getRole().name()).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + expiration)).signWith(key).compact();
    }

    public Claims parse(String t) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(t).getPayload();
    }
}
