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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CaddyAuthService {
    private final HouseCaddyRepository houseCaddyRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public void setPassword(SigninRequest.Password request) {
        HouseCaddy caddy = houseCaddyRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 캐디입니다."));
        caddy.updatePassword(request.getPassword());
    }

    @Transactional
    public SigninResponse.Caddy afterLogin(SigninRequest.Caddy request) {
        HouseCaddy caddy = houseCaddyRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 캐디입니다."));
        if (caddy.getPassword() == null) {
            throw new IllegalArgumentException("비밀번호가 설정되지 않았습니다.");
        }
        //TODO: 인코딩 된 비밀번호 match 검사하도록 수정 필요
        if (!caddy.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        JwtResponse jwtResponse = jwtProvider.issueTokens(Role.ROLE_HOUSE_CADDY, caddy.getPhoneNumber(), caddy.getId());
        return SigninResponse.Caddy.from(jwtResponse, caddy, Role.ROLE_HOUSE_CADDY);
    }

    @Transactional
    public SigninResponse.Caddy firstLogin(SigninRequest.First request) {
        HouseCaddy caddy = houseCaddyRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 캐디입니다."));
        JwtResponse jwtResponse = jwtProvider.issueTokens(Role.ROLE_HOUSE_CADDY, caddy.getPhoneNumber(), caddy.getId());
        return SigninResponse.Caddy.from(jwtResponse, caddy, Role.ROLE_HOUSE_CADDY);
    }
}
