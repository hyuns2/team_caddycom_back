package com.flash21.caddycom.service.auth;

import  com.flash21.caddycom.dto.auth.JwtRequest;
import com.flash21.caddycom.dto.auth.JwtResponse;
import com.flash21.caddycom.dto.auth.SigninRequest;
import com.flash21.caddycom.dto.auth.SigninResponse;
import com.flash21.caddycom.entity.account.Account;
import com.flash21.caddycom.entity.account.Role;
import com.flash21.caddycom.global.common.fileReader.CellValueConverter;
import com.flash21.caddycom.global.jwt.JwtProvider;
import com.flash21.caddycom.repository.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AccountRepository accountRepository;
    private final JwtProvider jwtProvider;
    private final CellValueConverter cellValueConverter;


    /**
     * 1. 전화번호만 입력받아서 해당 전화번호의 직원이 존재하는지 확인
     * 2. 계정이 존재하는 경우 직원이다. (골프장 등록을 한 사장이 다시 최초 접속을 한 경우 -> 개발환경에서는 열어둠)
     * 3. 계정이 존재하는 사람에 대해서는 jwt 토큰을 발급해주고, 해당 사람이 속한 골프장 정보를 같이 반환함.
     * 4. 계정이 존재하지 않으면 골프장 등록을 하지 않은 사장님으로 간주함.
     */
    @Transactional
    public SigninResponse.Main firstLogin(SigninRequest.First request) {
        Optional<Account> accountOpt = accountRepository.findByPhoneNumber(request.getPhoneNumber());

        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            JwtResponse jwtResponse = jwtProvider.issueTokens(account.getRole(), account.getPhoneNumber(), account.getId());

            if (account.getRole() == Role.ROLE_EMPLOYEE ||
                    (account.getRole() == Role.ROLE_OWNER && account.getGolfField() != null)) {
                return SigninResponse.Main.from(jwtResponse, account.getGolfField(), account.getRole(), account.getPassword());
            }
        }

        // TODO: 골프장 등록 안한 사장 로그아웃 후 다시 접속했을때 duplicate phoneNumber 에러 핸들링
        // 사장님 최초 로그인
        String phoneNumber = cellValueConverter.convertPhoneNumber(request.getPhoneNumber());
        Account owner = Account.builder()
                .phoneNumber(phoneNumber)
                .role(Role.ROLE_OWNER)
                .build();
        accountRepository.save(owner);
        return SigninResponse.Main.first();
    }



    /**
     * 1. 전화번호와 비밀번호를 입력받아서 해당 전화번호의 직원/사장이 존재하는지 확인
     * 2. 계정이 존재하는 경우 비밀번호가 일치하는지 확인
     * 3. 비밀번호가 일치하는 경우 jwt 토큰을 발급해주고, 해당 사람이 속한 골프장 정보를 같이 반환함.
     */
    @Transactional
    public SigninResponse.Main afterLogin(SigninRequest.Login request) {
        Account account = accountRepository.findByPhoneNumber(request.getPhoneNumber()).
                orElseThrow(() -> new NoSuchElementException("해당 전화번호의 직원/사장은 존재하지 않습니다."));

        if (account.getPassword() == null)
            throw new IllegalArgumentException("비밀번호가 아직 설정되지 않았습니다.");

        //TODO: 인코딩 된 비밀번호 match 검사하도록 수정 필요
        if (!account.getPassword().equals(request.getPassword()))
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");

        JwtResponse jwtResponse = jwtProvider.issueTokens(account.getRole(), account.getPhoneNumber(), account.getId());
        return SigninResponse.Main.from(jwtResponse, account.getGolfField(), account.getRole(), account.getPassword());
    }



    /**
     * 1. 전화번호를 입력받아서 해당 전화번호의 직원이 존재하는지 확인
     * 2. 계정이 존재하는 경우 비밀번호를 변경 ( 변경 시 유효한 비밀번호인지 확인, 인코딩하여 저장)
     */
    @Transactional
    public void setPassword(SigninRequest.Password request) {
        Account account = accountRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new NoSuchElementException("해당 전화번호의 직원은 존재하지 않습니다."));

        //TODO: 비밀번호 인코딩하여 저장
        if (!isPasswordValid(request.getPassword()))
            throw new IllegalArgumentException("비밀번호는 6자리 숫자로 입력해주세요.");
        account.updatePassword(request.getPassword());
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
