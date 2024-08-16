package com.flash21.caddycom.global.jwt;

import com.flash21.caddycom.dto.auth.JwtResponse;
import com.flash21.caddycom.entity.account.GolfStaff;
import com.flash21.caddycom.entity.account.Role;
import com.flash21.caddycom.entity.caddy.Caddy;
import com.flash21.caddycom.repository.golfStaff.GolfStaffRepository;
import com.flash21.caddycom.repository.caddy.CaddyRepository;
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
import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${jwt.secret}")
    private String jwtSecret;
    private final long ACCESS_EXPIRAION = 1000 * 60 * 60 * 1; // 1시간
    private final long REFRESH_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일

    private final GolfStaffRepository golfStaffRepository;
    private final CaddyRepository caddyRepository;
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

        saveRefreshToken(role, id, refreshToken);

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveRefreshToken(Role role, Long id, String refreshToken) {
        if (role == Role.ROLE_EMPLOYEE || role == Role.ROLE_OWNER) {
            GolfStaff golfStaff = golfStaffRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("사라진 사용자 계정입니다."));
            golfStaff.updateToken(refreshToken);
        }
        else if (role == Role.ROLE_HOUSE_CADDY || role == Role.ROLE_FREE_CADDY) {
            Caddy caddy = caddyRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("사라진 사용자 계정입니다."));
            caddy.updateToken(refreshToken);
        }

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
