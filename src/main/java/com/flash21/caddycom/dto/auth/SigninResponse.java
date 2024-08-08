package com.flash21.caddycom.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.flash21.caddycom.entity.account.Role;
import com.flash21.caddycom.entity.caddy.Caddy;
import com.flash21.caddycom.entity.caddy.HouseCaddy;
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
        private boolean isSetup;

        @Getter
        public enum Status {
            DONE, WAITING, YET
        }

        private static Status getStatus(ApprovalStatus status){
            if (status == ApprovalStatus.APPROVED) return Status.DONE;
            else if (status == ApprovalStatus.WAITING) return Status.WAITING;
            else return Status.YET;
        }

        public static Main from(JwtResponse jwtResponse, GolfField golfField, Role role, String password){
            // 골프장 등록 안한 사장님 재접속시 방어로직
            if (golfField == null)
                return Main.builder()
                        .role(role.toString().substring(5))
                        .status(Status.YET)
                        .isSetup(password != null)
                        .build();

            if (golfField.getStatus() == ApprovalStatus.WAITING)
                return Main.builder()
                        .role(role.toString().substring(5))
                        .accessToken(jwtResponse.getAccessToken())
                        .refreshToken(jwtResponse.getRefreshToken())
                        .status(Status.WAITING)
                        .isSetup(password != null)
                        .build();

            if (golfField.getStatus() == ApprovalStatus.REJECTED)
                return Main.builder()
                        .role(role.toString().substring(5))
                        .status(Status.YET)
                        .isSetup(password != null)
                        .build();

            else return Main.builder()
                        .role(role.toString().substring(5))
                        .accessToken(jwtResponse.getAccessToken())
                        .refreshToken(jwtResponse.getRefreshToken())
                        .golfFieldId(golfField.getId())
                        .golfFieldName(golfField.getName())
                        .golfFieldImage(golfField.getImageUrl())
                        .status(Status.DONE)
                        .isSetup(password != null)
                        .build();
        }


        public static Main first(){
            return Main.builder()
                    .role("OWNER")
                    .status(Status.YET)
                    .isSetup(false)
                    .build();
        }

    }


    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    @Getter
    public static class CaddyMain {
        private String accessToken;
        private String refreshToken;
        private Long caddyId;
        private String caddyName;
        private Long golfFieldId;
        private String team;
        private String teamRole;
        private Long point;
        private boolean isSetup;
        private String role;

        public static CaddyMain from(JwtResponse jwtResponse, Caddy caddy) {
            CaddyMain.CaddyMainBuilder builder = CaddyMain.builder()
                    .accessToken(jwtResponse.getAccessToken())
                    .refreshToken(jwtResponse.getRefreshToken())
                    .caddyId(caddy.getId())
                    .caddyName(caddy.getName() == null ? "사용자" : caddy.getName())
                    .point(caddy.getPoint())
                    .role(caddy.getType().toString().substring(5))
                    .isSetup(caddy.getPassword() != null);

            if (caddy instanceof HouseCaddy houseCaddy)
                builder.golfFieldId(houseCaddy.getGolfField().getId())
                        .team(houseCaddy.getTeam() == null ? "조 없음" : houseCaddy.getTeam())
                        .teamRole(houseCaddy.getTeamRole() == null ? "" : houseCaddy.getTeamRole().name());

            return builder.build();
        }
    }
}
