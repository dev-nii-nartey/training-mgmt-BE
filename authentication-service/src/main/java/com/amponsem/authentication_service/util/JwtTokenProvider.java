package com.amponsem.authentication_service.util;

import com.amponsem.authentication_service.exception.CustomException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Component
public class JwtTokenProvider {

    @Value("${spring.app.jwtSecret}")
    private String secretKey;


    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsername(String token) {
        return getClaims(token).get("email", String.class); // Extract email claim from the token
    }

    public List<SimpleGrantedAuthority> getAuthorities(String token) {
        String role = getClaims(token).get("role", String.class);
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            return token;
        }
        return null;
    }

    public boolean validateToken(String token) {
        if (token == null) {
            throw new CustomException("Token is null", HttpStatus.UNAUTHORIZED);
        }

        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            throw new CustomException("JWT token has expired", HttpStatus.UNAUTHORIZED);
        } catch (MalformedJwtException | SignatureException ex) {
            throw new CustomException("Invalid JWT token", HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            throw new CustomException("JWT token validation failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}