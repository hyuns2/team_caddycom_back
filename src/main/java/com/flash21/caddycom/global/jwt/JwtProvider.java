package com.flash21.caddycom.global.jwt;

import com.flash21.caddycom.dto.auth.JwtResponse;
import com.flash21.caddycom.entity.account.Role;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.repository.GolfFieldRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;


import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${jwt.secret}")
    private String jwtSecret;
    private final long ACCESS_EXPIRAION = 1000 * 60 * 60 * 3; // 3시간
    private final long REFRESH_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일
    private final GolfFieldRepository golfFieldRepository;

    public JwtResponse issueTokens(Role role, String name, Long id) {
        long current = System.currentTimeMillis();
        Date accessTokenExpireTime = new Date(current + ACCESS_EXPIRAION);
        Date refreshTokenExpireTime = new Date(current + REFRESH_EXPIRATION);

        Map<String, Object> claims = new HashMap<>();

        if (role== Role.ROLE_MANAGER) {
            GolfField golfField = golfFieldRepository.findById(id)
                    .orElseThrow(() -> new JwtException("올바르지 않은 사용자 정보를 담은 토큰입니다."));

            JwtClaims jwtClaims = JwtClaims.builder()
                    .id(golfField.getId())
                    .name(golfField.getName())
                    .role(Role.ROLE_MANAGER)
                    .build();
            claims.put("jwtClaims", jwtClaims);
        }
        else if (role == Role.ROLE_ADMIN) {
            JwtClaims jwtClaims = JwtClaims.builder()
                    .id(1L)
                    .name("관리자")
                    .role(Role.ROLE_ADMIN)
                    .build();
            claims.put("jwtClaims", jwtClaims);
        }


        String accessToken = generateToken(accessTokenExpireTime, claims);

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(accessToken)
                .build();
    }

    private String generateToken(Date expiration, Map<String,?> claims) {
        Key secretKey = createSignature();

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }


    protected Key createSignature() {
        byte[] secretBytes = DatatypeConverter.parseBase64Binary(jwtSecret);
        return new SecretKeySpec(secretBytes, SignatureAlgorithm.HS256.getJcaName());
    }
}
