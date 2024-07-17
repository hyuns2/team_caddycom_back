package com.flash21.caddycom.dto.auth;

import com.flash21.caddycom.entity.account.Role;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class SigninResponse {
    private Role role;
    private String accessToken;
    private String refreshToken;
    private Long golfFieldId;

    public SigninResponse(JwtResponse jwtResponse, Long golfFieldId) {
        this.role = Role.ROLE_MANAGER;
        this.accessToken = jwtResponse.getAccessToken();
        this.refreshToken = jwtResponse.getRefreshToken();
        this.golfFieldId = golfFieldId;
    }

    public SigninResponse(JwtResponse jwtResponse) {
        this.role = Role.ROLE_ADMIN;
        this.accessToken = jwtResponse.getAccessToken();
        this.refreshToken = jwtResponse.getRefreshToken();
    }
}
