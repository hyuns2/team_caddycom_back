package com.flash21.caddycom.service.auth;

import  com.flash21.caddycom.dto.auth.JwtRequest;
import com.flash21.caddycom.dto.auth.JwtResponse;
import com.flash21.caddycom.dto.auth.SigninRequest;
import com.flash21.caddycom.dto.auth.SigninResponse;
import com.flash21.caddycom.entity.account.GolfStaff;
import com.flash21.caddycom.entity.account.Role;
import com.flash21.caddycom.global.jwt.JwtProvider;
import com.flash21.caddycom.repository.account.GolfStaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final GolfStaffRepository golfStaffRepository;
    private final JwtProvider jwtProvider;


    /**
     * 1. 전화번호만 입력받아서 해당 전화번호의 직원이 존재하는지 확인
     * 2. 계정이 존재하는 경우 직원이다. (골프장 등록을 한 사장이 다시 최초 접속을 한 경우 -> 개발환경에서는 열어둠)
     * 3. 계정이 존재하는 사람에 대해서는 jwt 토큰을 발급해주고, 해당 사람이 속한 골프장 정보를 같이 반환함.
     * 4. 계정이 존재하지 않으면 골프장 등록을 하지 않은 사장님으로 간주함.
     */
    @Transactional
    public SigninResponse.Main firstLogin(SigninRequest.First request) {
        Optional<GolfStaff> golfStaffOpt = golfStaffRepository.findByPhoneNumber(request.getPhoneNumber());

        if (golfStaffOpt.isPresent()) {
            GolfStaff golfStaff = golfStaffOpt.get();
            JwtResponse jwtResponse = jwtProvider.issueTokens(golfStaff.getRole(), golfStaff.getPhoneNumber(), golfStaff.getId());

            if (golfStaff.getRole() == Role.ROLE_EMPLOYEE ||
                    (golfStaff.getRole() == Role.ROLE_OWNER && golfStaff.getGolfField() != null)) {
                return SigninResponse.Main.from(jwtResponse, golfStaff.getGolfField(), golfStaff.getRole(), golfStaff.getPassword());
            }
        }

        // 사장님 최초 로그인
        if (golfStaffOpt.isEmpty()){
            GolfStaff owner = GolfStaff.builder()
                    .phoneNumber(request.getPhoneNumber())
                    .role(Role.ROLE_OWNER)
                    .build();
            golfStaffRepository.save(owner);
        }
        return SigninResponse.Main.first();
    }



    /**
     * 1. 전화번호와 비밀번호를 입력받아서 해당 전화번호의 직원/사장이 존재하는지 확인
     * 2. 계정이 존재하는 경우 비밀번호가 일치하는지 확인
     * 3. 비밀번호가 일치하는 경우 jwt 토큰을 발급해주고, 해당 사람이 속한 골프장 정보를 같이 반환함.
     */
    @Transactional
    public SigninResponse.Main afterLogin(SigninRequest.Login request) {
        GolfStaff golfStaff = golfStaffRepository.findByPhoneNumber(request.getPhoneNumber()).
                orElseThrow(() -> new NoSuchElementException("해당 전화번호의 직원/사장은 존재하지 않습니다."));

        if (golfStaff.getPassword() == null)
            throw new IllegalArgumentException("비밀번호가 아직 설정되지 않았습니다.");

        //TODO: 인코딩 된 비밀번호 match 검사하도록 수정 필요
        if (!golfStaff.getPassword().equals(request.getPassword()))
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");

        JwtResponse jwtResponse = jwtProvider.issueTokens(golfStaff.getRole(), golfStaff.getPhoneNumber(), golfStaff.getId());
        return SigninResponse.Main.from(jwtResponse, golfStaff.getGolfField(), golfStaff.getRole(), golfStaff.getPassword());
    }



    /**
     * 1. 전화번호를 입력받아서 해당 전화번호의 직원이 존재하는지 확인
     * 2. 계정이 존재하는 경우 비밀번호를 변경 ( 변경 시 유효한 비밀번호인지 확인, 인코딩하여 저장)
     */
    @Transactional
    public void setPassword(SigninRequest.Password request) {
        GolfStaff golfStaff = golfStaffRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new NoSuchElementException("해당 전화번호의 직원은 존재하지 않습니다."));

        //TODO: 비밀번호 인코딩하여 저장
        if (!isPasswordValid(request.getPassword()))
            throw new IllegalArgumentException("비밀번호는 6자리 숫자로 입력해주세요.");
        golfStaff.updatePassword(request.getPassword());
    }


    private boolean isPasswordValid(String password) {
        return password.length() == 6 &&
                password.chars().allMatch(Character::isDigit);
    }


    @Transactional
    public JwtResponse reissueTokens(JwtRequest request) {
        return jwtProvider.reissueTokens(request.getRefreshToken());
    }
}
