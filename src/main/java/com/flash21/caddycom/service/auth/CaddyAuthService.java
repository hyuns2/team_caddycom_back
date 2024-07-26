package com.flash21.caddycom.service.auth;

import com.flash21.caddycom.dto.auth.SigninRequest;
import com.flash21.caddycom.dto.auth.SigninResponse;
import com.flash21.caddycom.repository.caddy.HouseCaddyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CaddyAuthService {
    private final HouseCaddyRepository houseCaddyRepository;

    public void setPassword(SigninRequest.Password request) {
        // 비밀번호 설정
    }

    public SigninResponse.Caddy afterLogin(SigninRequest.Login request) {
        // 로그인
        return null;
    }

    public SigninResponse.Caddy firstLogin(SigninRequest.First request) {
        // 최초 로그인
        return null;
    }
}
