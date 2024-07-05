package com.flash21.caddycom.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SigninRequest {
    @NotBlank(message = "key는 필수값입니다.")
    private String key;

    @NotBlank(message = "password는 필수값입니다.")
    private String password;
}
