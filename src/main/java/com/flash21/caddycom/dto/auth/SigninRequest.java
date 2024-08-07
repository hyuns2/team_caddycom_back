package com.flash21.caddycom.dto.auth;

import com.flash21.caddycom.global.validation.PhoneNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class SigninRequest {

    //TODO: 전화번호 파싱 정규식 공통 메서드 추출 필요
    private static String formatPhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll("[^0-9]", "");
    }

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


    @AllArgsConstructor
    @NoArgsConstructor
    public static class First {
        @NotBlank(message = "phoneNumber는 필수값입니다.")
        @PhoneNumber
        private String phoneNumber;

        public String getPhoneNumber() {
            return formatPhoneNumber(phoneNumber);
        }
    }


    @AllArgsConstructor
    @NoArgsConstructor
    public static class Login {
        @NotBlank(message = "phoneNumber는 필수값입니다.")
        @PhoneNumber
        private String phoneNumber;

        @Getter
        @NotBlank(message = "password는 필수값입니다.")
        private String password;

        public String getPhoneNumber() {
            return formatPhoneNumber(phoneNumber);
        }
    }


    @AllArgsConstructor
    @NoArgsConstructor
    public static class Password {
        @NotBlank(message = "phoneNumber는 필수값입니다.")
        @PhoneNumber
        private String phoneNumber;

        @Getter
        @NotBlank(message = "password는 필수값입니다.")
        public String password;

        public String getPhoneNumber() {
            return formatPhoneNumber(phoneNumber);
        }
    }


    @AllArgsConstructor
    @NoArgsConstructor
    public static class Caddy {
        @Getter
        @NotBlank(message = "type은 필수값입니다.")
        private String type;

        @NotBlank(message = "phoneNumber는 필수값입니다.")
        @PhoneNumber
        private String phoneNumber;

        @Getter
        @NotBlank(message = "password는 필수값입니다.")
        private String password;

        public String getPhoneNumber() {
            return formatPhoneNumber(phoneNumber);
        }
    }

}
