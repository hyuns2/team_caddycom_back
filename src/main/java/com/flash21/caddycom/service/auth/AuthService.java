package com.flash21.caddycom.service.auth;

import com.flash21.caddycom.dto.auth.JwtResponse;
import com.flash21.caddycom.dto.auth.SigninRequest;
import com.flash21.caddycom.entity.account.Role;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.global.jwt.JwtProvider;
import com.flash21.caddycom.repository.GolfFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final GolfFieldRepository golfFieldRepository;

    public JwtResponse login(SigninRequest request){
        if (request.getKey().equals("관리자")){
            return adminLogin(request.getPassword());
        }
        GolfField golfField = golfFieldRepository.findByRegistrationNumber(request.getKey())
                .orElseThrow(() -> new IllegalArgumentException("해당 사업자 등록번호의 골프장은 존재하지 않습니다."));
        return managerLogin(request.getPassword(), golfField);
    }

    private JwtResponse adminLogin(String password){
        if (!password.equals("123456"))
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        return jwtProvider.issueTokens(Role.ROLE_ADMIN,"관리자",1L);
    }

    private JwtResponse managerLogin(String password, GolfField golfField){
        if (!golfField.getPassword().equals(password))
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        return jwtProvider.issueTokens(Role.ROLE_MANAGER,golfField.getName(),golfField.getId());
    }

}
