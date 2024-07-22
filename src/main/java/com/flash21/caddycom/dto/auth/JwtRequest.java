package com.flash21.caddycom.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class JwtRequest {
    @NotBlank(message = "refreshToken은 필수값입니다.")
    public String refreshToken;
}
