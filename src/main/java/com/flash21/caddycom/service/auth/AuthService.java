package com.flash21.caddycom.service.auth;

import com.flash21.caddycom.dto.auth.SigninResponse;
import com.flash21.caddycom.dto.auth.SigninRequest;
import com.flash21.caddycom.entity.account.Role;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.global.jwt.JwtProvider;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final GolfFieldRepository golfFieldRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 관리자 계정은 우선 id: 1L, name: 관리자, password: '123456'으로 한다.
     */
    @Transactional(readOnly = true)
    public SigninResponse login(SigninRequest request){
        if (request.getKey().equals("관리자")){
            return adminLogin(request.getPassword());
        }
        GolfField golfField = golfFieldRepository.findByRegistrationNumber(request.getKey())
                .orElseThrow(() -> new IllegalArgumentException("해당 사업자 등록번호의 골프장은 존재하지 않습니다."));
        return managerLogin(request.getPassword(), golfField);
    }

    private SigninResponse adminLogin(String password){
        if (!passwordEncoder.matches(password, passwordEncoder.encode("123456")))
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");

        return jwtProvider.issueTokens(Role.ROLE_ADMIN, "관리자", 1L);
    }

    private SigninResponse managerLogin(String password, GolfField golfField){
        if (!passwordEncoder.matches(password, golfField.getPassword()))
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");

        return jwtProvider.issueTokens(Role.ROLE_MANAGER, golfField.getName(), golfField.getId());
    }

}
