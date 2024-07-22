package com.flash21.caddycom.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.flash21.caddycom.entity.account.Account;
import com.flash21.caddycom.entity.account.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@AllArgsConstructor
@Builder
@Getter
public class SigninResponse {

    @AllArgsConstructor
    @Builder
    @Getter
    public static class Web {
        private Role role;
        private String accessToken;
        private String refreshToken;
        private Long golfFieldId;

        public static SigninResponse.Web from(JwtResponse jwtResponse, Long golfFieldId) {
            return SigninResponse.Web.builder()
                    .role(Role.ROLE_OWNER)
                    .accessToken(jwtResponse.getAccessToken())
                    .refreshToken(jwtResponse.getRefreshToken())
                    .golfFieldId(golfFieldId)
                    .build();
        }

        public static SigninResponse.Web from(JwtResponse jwtResponse) {
            return SigninResponse.Web.builder()
                    .role(Role.ROLE_ADMIN)
                    .accessToken(jwtResponse.getAccessToken())
                    .refreshToken(jwtResponse.getRefreshToken())
                    .build();
        }
    }


    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    @Getter
    public static class First {
        private String role;
        private String accessToken;
        private String refreshToken;
        private Long golfFieldId;
        private String name;
        private String imageUrl;
        private Status status;

        @Getter
        public enum Status {
            DONE, WAITING, YET
        }

        public static SigninResponse.First from(JwtResponse jwtResponse, Long golfFieldId, String name, String imageUrl) {
            return First.builder()
                    .role("EMPLOYEE")
                    .accessToken(jwtResponse.getAccessToken())
                    .refreshToken(jwtResponse.getRefreshToken())
                    .golfFieldId(golfFieldId)
                    .name(name)
                    .imageUrl(imageUrl)
                    .status(Status.DONE)
                    .build();
        }

        public static SigninResponse.First from(Status status, JwtResponse jwtResponse){
            return SigninResponse.First.builder()
                    .role("OWNER")
                    .accessToken(jwtResponse.getAccessToken())
                    .refreshToken(jwtResponse.getRefreshToken())
                    .status(status)
                    .build();
        }

        public static SigninResponse.First from(Status status){
            return SigninResponse.First.builder()
                    .role("OWNER")
                    .status(status)
                    .build();
        }


    }

    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    @Builder
    public static class After {
        private String role;
        private String accessToken;
        private String refreshToken;
        private Long golfFieldId;
        private String name;
        private String imageUrl;
        private Status status;

        @Getter
        public enum Status {
            DONE, WAITING, YET
        }

        public static SigninResponse.After from(JwtResponse jwtResponse, Account account) {
            return SigninResponse.After.builder()
                    .role(account.getRole().toString().substring(5))
                    .accessToken(jwtResponse.getAccessToken())
                    .refreshToken(jwtResponse.getRefreshToken())
                    .golfFieldId(account.getGolfField().getId())
                    .name(account.getGolfField().getName())
                    .imageUrl(account.getGolfField().getImageUrl())
                    .status(Status.DONE)
                    .build();
        }

        public static SigninResponse.After from(JwtResponse jwtResponse, Role role) {
            return SigninResponse.After.builder()
                    .role(role.toString().substring(5))
                    .accessToken(jwtResponse.getAccessToken())
                    .refreshToken(jwtResponse.getRefreshToken())
                    .status(Status.WAITING)
                    .build();
        }

    }
}
