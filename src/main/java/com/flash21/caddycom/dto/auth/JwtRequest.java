package com.flash21.caddycom.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtRequest {
    @NotBlank(message = "refreshToken은 필수값입니다.")
    public final String refreshToken;
}
