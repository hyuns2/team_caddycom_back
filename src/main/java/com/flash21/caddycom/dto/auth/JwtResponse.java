package com.flash21.caddycom.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
}
