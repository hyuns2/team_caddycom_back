package com.flash21.caddycom.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flash21.caddycom.entity.account.Account;
import com.flash21.caddycom.entity.account.Role;
import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.repository.account.AccountRepository;
import com.flash21.caddycom.repository.caddy.HouseCaddyRepository;
import io.jsonwebtoken.*;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;


@Component
@RequiredArgsConstructor
public class JwtValidator {

    @Value("${jwt.secret}")
    private String jwtSecret;
    private final AccountRepository accountRepository;
    private final HouseCaddyRepository houseCaddyRepository;

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
        Key key = createSignature();
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "만료된 토큰입니다.");
        } catch (Exception e) {
            throw new JwtException("토큰 파싱 중 오류 발생. 유효하지 않은 토큰입니다.", e);
        }
    }

    private Key createSignature() {
        byte[] secretBytes = DatatypeConverter.parseBase64Binary(jwtSecret);
        return new SecretKeySpec(secretBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public JwtClaims checkRefreshToken(String refreshToken) {
        Claims claims = extractClaims(refreshToken);
        ObjectMapper mapper = new ObjectMapper();
        JwtClaims jwtClaims = mapper.convertValue(claims.get("jwtClaims"), JwtClaims.class);

        if (jwtClaims.getRole() == Role.ROLE_EMPLOYEE || jwtClaims.getRole() == Role.ROLE_OWNER) {
            Account account = accountRepository.findById(jwtClaims.getId())
                    .orElseThrow(() -> new JwtException("올바르지 않은 사용자 정보를 담은 토큰입니다."));
            if (!account.getRefreshToken().equals(refreshToken))
                throw new JwtException("올바르지 않은 리프레시 토큰입니다.");

        } else if (jwtClaims.getRole() == Role.ROLE_HOUSE_CADDY) {
            HouseCaddy caddy = houseCaddyRepository.findById(jwtClaims.getId())
                    .orElseThrow(() -> new JwtException("올바르지 않은 사용자 정보를 담은 토큰입니다."));
            if (!caddy.getRefreshToken().equals(refreshToken))
                throw new JwtException("올바르지 않은 리프레시 토큰입니다.");
        }
        return jwtClaims;
    }
}
