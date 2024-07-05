package com.flash21.caddycom.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JwtValidator {
    private JwtProvider jwtProvider;
    /**
     * 추후 리프레시 토큰 검증에도 동일한 로직을 수행하기 위해 메서드추출
     */
    private String verifyToken(String token){
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("유효하지 않은 형식의 토큰입니다.");
        }
        return token.substring(7);
    }

    public String getAccessToken(String string){
        return verifyToken(string);
    }

    public Claims extractClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(jwtProvider.createSignature()).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new JwtException("만료된 토큰입니다.");
        }
    }
}
