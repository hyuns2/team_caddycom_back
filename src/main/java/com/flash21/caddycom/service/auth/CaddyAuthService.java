package com.flash21.caddycom.service.auth;

import com.flash21.caddycom.dto.auth.JwtResponse;
import com.flash21.caddycom.dto.auth.SigninRequest;
import com.flash21.caddycom.dto.auth.SigninResponse;
import com.flash21.caddycom.entity.account.Role;
import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.global.jwt.JwtProvider;
import com.flash21.caddycom.repository.caddy.HouseCaddyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CaddyAuthService {
    private final HouseCaddyRepository houseCaddyRepository;
    private final JwtProvider jwtProvider;

    public void setPassword(SigninRequest.Password request) {
        HouseCaddy caddy = houseCaddyRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 캐디입니다."));
        caddy.updatePassword(request.getPassword());
    }

    public SigninResponse.Caddy afterLogin(SigninRequest.Login request) {
        // 로그인
        return null;
    }

    public SigninResponse.Caddy firstLogin(SigninRequest.First request) {
        HouseCaddy caddy = houseCaddyRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 캐디입니다."));
        JwtResponse jwtResponse = jwtProvider.issueTokens(Role.ROLE_HOUSE_CADDY, caddy.getPhoneNumber(), caddy.getId());
        return SigninResponse.Caddy.from(jwtResponse, caddy, Role.ROLE_HOUSE_CADDY);
    }
}
