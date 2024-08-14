package com.flash21.caddycom.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.flash21.caddycom.global.util.Formatter.formatPhoneNumber;

public class SigninCommand {
    @Getter
    @AllArgsConstructor
    public static class First {
        private String phoneNumber;

        public static SigninCommand.First from(SigninRequest.First request) {
            return new SigninCommand.First(
                    formatPhoneNumber(request.getPhoneNumber()));
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Login {
        private String phoneNumber;
        private String password;

       public static SigninCommand.Login from(SigninRequest.Login request) {
            return new SigninCommand.Login(
                    formatPhoneNumber(request.getPhoneNumber()),
                    request.getPassword());
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Password {
        private String phoneNumber;
        private String password;

        public static SigninCommand.Password from(SigninRequest.Password request) {
            return new SigninCommand.Password(
                    formatPhoneNumber(request.getPhoneNumber()),
                    request.getPassword());
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Caddy {
        private String phoneNumber;
        private String password;

        public static SigninCommand.Caddy from(SigninRequest.Caddy request) {
            return new SigninCommand.Caddy(
                    formatPhoneNumber(request.getPhoneNumber()),
                    request.getPassword());
        }
    }


}
