package com.flash21.caddycom.service.auth;

import com.flash21.caddycom.dto.auth.JwtResponse;
import com.flash21.caddycom.dto.auth.SigninRequest;
import com.flash21.caddycom.dto.auth.SigninResponse;
import com.flash21.caddycom.entity.account.Manager;
import com.flash21.caddycom.entity.account.Role;
import com.flash21.caddycom.global.jwt.JwtProvider;
import com.flash21.caddycom.repository.account.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManagerService {
    private final ManagerRepository managerRepository;
    private final JwtProvider jwtProvider;

    @Transactional(readOnly = true)
    public SigninResponse.First firstLogin(SigninRequest.First request) {
        Optional<Manager> manager = managerRepository.findByPhoneNumber(request.getPhoneNumber());
        if (manager.isPresent() && manager.get().getRole() == Role.ROLE_EMPLOYEE ) {
            JwtResponse jwts = jwtProvider.issueTokens(Role.ROLE_EMPLOYEE, manager.get().getName(), manager.get().getId());
            return SigninResponse.First.from(jwts, manager.get().getGolfField().getId(), manager.get().getGolfField().getName(), manager.get().getGolfField().getImageUrl());
        }
        else if (manager.isPresent() && manager.get().getRole() == Role.ROLE_MANAGER ) {
            return SigninResponse.First.from(SigninResponse.First.Status.WAITING);
        }
        else {
            return SigninResponse.First.from(SigninResponse.First.Status.YET);
        }
    }
}
