package com.flash21.caddycom.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;


public class SigninRequest {

    @Getter
    @AllArgsConstructor
    public static class Web {
        @NotBlank(message = "key는 필수값입니다.")
        @Schema(example = "사업자 등록 번호(String)")
        private final String key;

        @NotBlank(message = "password는 필수값입니다.")
        @Schema(example = "비밀번호 6자리(String)")
        private final String password;
    }

    @Getter
    @AllArgsConstructor
    public static class First {
        @NotBlank(message = "phoneNumber는 필수값입니다.")
        private final String phoneNumber;
    }

    @Getter
    @AllArgsConstructor
    public static class Login {
        @NotBlank(message = "phoneNumber는 필수값입니다.")
        private final String phoneNumber;

        @NotBlank(message = "password는 필수값입니다.")
        private final String password;
    }

    @Getter
    @AllArgsConstructor
    public static class Password {
        @NotBlank(message = "phoneNumber는 필수값입니다.")
        private final String phoneNumber;

        @NotBlank(message = "password는 필수값입니다.")
        public final String password;
    }

    @Getter
    @AllArgsConstructor
    public static class Caddy {
        @NotBlank(message = "type은 필수값입니다.")
        private final String type;

        @NotBlank(message = "phoneNumber는 필수값입니다.")
        private final String phoneNumber;

        @NotBlank(message = "password는 필수값입니다.")
        private final String password;
    }

}
