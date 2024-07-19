package com.flash21.caddycom.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.flash21.caddycom.entity.account.Role;
import jakarta.annotation.Nullable;
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
                    .role(Role.ROLE_MANAGER)
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
            return SigninResponse.First.builder()
                    .role("EMPLOYEE")
                    .accessToken(jwtResponse.getAccessToken())
                    .refreshToken(jwtResponse.getRefreshToken())
                    .golfFieldId(golfFieldId)
                    .name(name)
                    .imageUrl(imageUrl)
                    .build();
        }

        public static SigninResponse.First from(Status status){
            return SigninResponse.First.builder()
                    .role("OWNER")
                    .status(status)
                    .build();
        }


    }
}
