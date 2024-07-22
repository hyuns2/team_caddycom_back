package com.flash21.caddycom.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.flash21.caddycom.entity.account.Account;
import com.flash21.caddycom.entity.account.Role;
import com.flash21.caddycom.entity.golfField.ApprovalStatus;
import com.flash21.caddycom.entity.golfField.GolfField;
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
    public static class Main {
        private String role;
        private String accessToken;
        private String refreshToken;
        private Long golfFieldId;
        private String golfFieldName;
        private String golfFieldImage;
        private Status status;

        @Getter
        public enum Status {
            DONE, WAITING, YET
        }

        private static Status getStatus(ApprovalStatus status){
            if (status == ApprovalStatus.APPROVED) return Status.DONE;
            else if (status == ApprovalStatus.WAITING) return Status.WAITING;
            else return Status.YET;
        }

        public static Main from(JwtResponse jwtResponse, GolfField golfField, Role role){
            if (golfField.getStatus() == ApprovalStatus.WAITING)
                return Main.builder()
                        .role(role.toString().substring(5))
                        .accessToken(jwtResponse.getAccessToken())
                        .refreshToken(jwtResponse.getRefreshToken())
                        .status(Status.WAITING)
                        .build();

            if (golfField.getStatus() == ApprovalStatus.REJECTED)
                return Main.builder()
                        .role(role.toString().substring(5))
                        .status(Status.YET)
                        .build();

            else return Main.builder()
                    .role(role.toString().substring(5))
                    .accessToken(jwtResponse.getAccessToken())
                    .refreshToken(jwtResponse.getRefreshToken())
                    .golfFieldId(golfField.getId())
                    .golfFieldName(golfField.getName())
                    .golfFieldImage(golfField.getImageUrl())
                    .status(Status.DONE)
                    .build();
        }


        public static Main first(){
            return Main.builder()
                    .role("OWNER")
                    .status(Status.YET)
                    .build();
        }

    }

}
