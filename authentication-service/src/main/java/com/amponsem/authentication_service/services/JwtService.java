package com.amponsem.authentication_service.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.security.Key;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;


    @Value("${spring.app.jwt-expiration-ms}")
    public long EXPIRE_ACCESS_TOKEN;


    public static final String ISSUER = "TM_APPLICATION";

    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", username);
        claims.put("role", role);
        return createToken(claims);
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRE_ACCESS_TOKEN))
                .issuer(ISSUER)
                .signWith(getSignKey())
                .compact();
    }

}