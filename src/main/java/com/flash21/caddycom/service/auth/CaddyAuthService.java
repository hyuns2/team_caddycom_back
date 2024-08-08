package com.flash21.caddycom.service.auth;

import com.flash21.caddycom.dto.auth.JwtResponse;
import com.flash21.caddycom.dto.auth.SigninRequest;
import com.flash21.caddycom.dto.auth.SigninResponse;
import com.flash21.caddycom.entity.account.Role;
import com.flash21.caddycom.entity.caddy.Caddy;
import com.flash21.caddycom.entity.caddy.FreeCaddy;
import com.flash21.caddycom.global.jwt.JwtProvider;
import com.flash21.caddycom.repository.caddy.CaddyRepository;
import com.flash21.caddycom.repository.caddy.FreeCaddyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CaddyAuthService {
    private final CaddyRepository caddyRepository;
    private final FreeCaddyRepository freeCaddyRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public void setPassword(SigninRequest.Password request) {
        Caddy caddy = caddyRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 캐디입니다."));
        caddy.updatePassword(request.getPassword());
    }

    /**
     * 전화번호와 비밀번호를 이용해 캐디를 확인하고 , jwt와 유저 정보를 담은 응답을 반환한다.
     */
    @Transactional
    public SigninResponse.CaddyMain afterLogin(SigninRequest.Caddy request) {
        Caddy caddy = caddyRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 캐디입니다."));
        if (caddy.getPassword() == null) {
            throw new IllegalArgumentException("비밀번호가 설정되지 않았습니다.");
        }
        //TODO: 인코딩 된 비밀번호 match 검사하도록 수정 필요
        if (!caddy.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return generateTokenAndResponse(caddy);
    }

    /**
     * 1. 전화번호를 이용해 캐디를 확인한다.
     * 2. 전화번호를 찾을 수 없으면 프리캐디로 간주하고 새롭게 생성한다.
     * 3. jwt와 유저 정보를 담은 응답을 반환한다.
     */
    @Transactional
    public SigninResponse.CaddyMain firstLogin(SigninRequest.First request) {
        Caddy caddy = caddyRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseGet(() -> createFreeCaddy(request.getPhoneNumber()));
        return generateTokenAndResponse(caddy);
    }

    private FreeCaddy createFreeCaddy(String phoneNumber) {
        return freeCaddyRepository.save(FreeCaddy.builder()
                .phoneNumber(phoneNumber)
                .role(Role.ROLE_FREE_CADDY)
                .build());
    }


    private SigninResponse.CaddyMain generateTokenAndResponse(Caddy caddy) {
        if (caddy.getType() == null)
            throw new IllegalArgumentException("캐디 타입이 설정되지 않았습니다.");

        JwtResponse jwtResponse = jwtProvider.issueTokens(caddy.getType(), caddy.getPhoneNumber(), caddy.getId());
        return SigninResponse.CaddyMain.from(jwtResponse, caddy);
    }
}
