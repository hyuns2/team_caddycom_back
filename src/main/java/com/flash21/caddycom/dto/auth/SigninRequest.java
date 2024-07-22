package com.flash21.caddycom.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;


public class SigninRequest {

    @Getter
    public static class Web {
        @NotBlank(message = "key는 필수값입니다.")
        @Schema(example = "사업자 등록 번호(String)")
        private String key;

        @NotBlank(message = "password는 필수값입니다.")
        @Schema(example = "비밀번호 6자리(String)")
        private String password;
    }

    @Getter
    public static class First {
        @NotBlank(message = "phoneNumber는 필수값입니다.")
        private String phoneNumber;
    }

    @Getter
    public static class After {
        @NotBlank(message = "phoneNumber는 필수값입니다.")
        private String phoneNumber;

        @NotBlank(message = "phoneNumber는 필수값입니다.")
        private String password;
    }

}
