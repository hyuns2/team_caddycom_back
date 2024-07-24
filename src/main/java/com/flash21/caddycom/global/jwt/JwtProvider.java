package com.flash21.caddycom.global.jwt;

import com.flash21.caddycom.dto.auth.JwtResponse;
import com.flash21.caddycom.dto.auth.SigninResponse;
import com.flash21.caddycom.entity.account.Account;
import com.flash21.caddycom.entity.account.Role;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.repository.account.AccountRepository;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
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
    private final long ACCESS_EXPIRAION = 1000 * 60 * 60 * 1; // 1시간
    private final long REFRESH_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일

    private final AccountRepository accountRepository;
    private final JwtValidator jwtValidator;

    public JwtResponse issueTokens(Role role, String phoneNumber, Long id) {
        long current = System.currentTimeMillis();
        Date accessTokenExpireTime = new Date(current + ACCESS_EXPIRAION);
        Date refreshTokenExpireTime = new Date(current + REFRESH_EXPIRATION);

        Map<String, Object> claims = new HashMap<>();

        JwtClaims jwtClaims = JwtClaims.builder()
                .id(id)
                .phoneNumber(phoneNumber)
                .role(role)
                .build();
        claims.put("jwtClaims", jwtClaims);


        String accessToken = generateToken(accessTokenExpireTime, claims);
        String refreshToken = generateToken(refreshTokenExpireTime, claims);

        saveRefreshToken(id, refreshToken);

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveRefreshToken(Long id, String refreshToken) {
        Account account = accountRepository.findById(id).orElseThrow();
        account.updateToken(refreshToken);
    }

    private String generateToken(Date expiration, Map<String,?> claims) {
        Key secretKey = createSignature();

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }


    private Key createSignature() {
        byte[] secretBytes = DatatypeConverter.parseBase64Binary(jwtSecret);
        return new SecretKeySpec(secretBytes, SignatureAlgorithm.HS256.getJcaName());
    }


    public JwtResponse reissueTokens(String token) {
        JwtClaims jwtClaims = jwtValidator.checkRefreshToken(token);
        return issueTokens(jwtClaims.getRole(), jwtClaims.getPhoneNumber(), jwtClaims.getId());
    }



}
