package com.dennis.api_getway.util;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    @Value("${spring.app.jwtSecret}")
    private String secret;

    public void validateToken(final String token) {

        if (token == null) {
            throw new JwtException("Token is null");
        }

        try {
            Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token);
        } catch (ExpiredJwtException ex) { 
            throw new JwtException("JWT token has expired");
        } catch (MalformedJwtException | SignatureException ex) { 
            throw new JwtException("Invalid JWT token");
        } catch (Exception ex) {
            throw new JwtException("JWT token validation failed");
        }
    }

    public String extractEmail(String token) {
        return  getClaims(token).get("email", String.class);
    }

    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public String extractPermissions(String token) {
        return getClaims(token).get("permissions", String.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

