package com.flash21.caddycom.service.auth;

import com.flash21.caddycom.dto.auth.JwtResponse;
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

/**
 * 로그인 요청 처리
 *
 * @see JwtProvider : 토큰 발급을 위한 class
 * @see GolfFieldRepository : 골프장 조회를 위한 repository
 * @see PasswordEncoder : 비밀번호 암호화를 위한 class
 * @author kwonssshyeon
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final GolfFieldRepository golfFieldRepository;
    private final PasswordEncoder passwordEncoder;


    /**
     * 로그인 요청 처리
     * <p>
     *     전체 시스템 관리자와 골프장 관리자를 key를 이용해 구분한다.
     *     관리자 계정은 고정된 계정으로 관리한다.(id: 1L, name: 관리자, password: '123456')
     *     골프장 관리자는 골프장의 사업자 등록번호로 로그인한다.
     * </p>
     * @param request 로그인 요청 DTO(key, password)
     * @return SigninResponse (aceessToken, refreshToken)
     * @throws IllegalArgumentException 비밀번호가 일치하지 않는 경우, 골프장이 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public SigninResponse.Web login(SigninRequest.Web request){
        if (request.getKey().equals("관리자")){
            JwtResponse jwtPair = adminLogin(request.getPassword());
            return SigninResponse.Web.from(jwtPair);
        }
        GolfField golfField = golfFieldRepository.findByRegistrationNumber(request.getKey())
                .orElseThrow(() -> new IllegalArgumentException("해당 사업자 등록번호의 골프장은 존재하지 않습니다."));
        JwtResponse jwtPair = managerLogin(request.getPassword(), golfField);
        return SigninResponse.Web.from(jwtPair, golfField.getId());
    }


    private JwtResponse adminLogin(String password){
        if (!passwordEncoder.matches(password, passwordEncoder.encode("123456")))
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");

        return jwtProvider.issueTokens(Role.ROLE_ADMIN, "관리자", 1L);
    }


    private JwtResponse managerLogin(String password, GolfField golfField){
        // 더미데이터를 위한 비밀번호 encode 비활성화
        if (!passwordEncoder.matches(password, passwordEncoder.encode(golfField.getPassword())))
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");

        return jwtProvider.issueTokens(Role.ROLE_MANAGER, golfField.getName(), golfField.getId());
    }

}
