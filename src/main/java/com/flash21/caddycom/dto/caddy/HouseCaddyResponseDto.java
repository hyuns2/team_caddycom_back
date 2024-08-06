package com.flash21.caddycom.dto.caddy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flash21.caddycom.entity.caddy.Days;
import com.flash21.caddycom.entity.caddy.Gender;
import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.entity.caddy.TeamRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class HouseCaddyResponseDto {
    @Getter
    @AllArgsConstructor
    public static class TeamHoliday {
        private String team;
        private List<HolidayInfo> info;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Info {
        private Long caddyId;
        @JsonIgnore
        private String team;
        private TeamRole teamRole;
        private String name;
        private List<Days> availDates;

        public Info(HouseCaddy houseCaddy) {
            this.caddyId = houseCaddy.getId();
            this.team = houseCaddy.getTeam();
            this.teamRole = houseCaddy.getTeamRole();
            this.name = houseCaddy.getName();
            this.availDates = houseCaddy.getHoliday();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class HolidayInfo{
        private Long id;
        private String name;
        private TeamRole teamRole;
        private List<Days> holiday;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class houseCaddyDetail {
        @Schema(description = "캐디 Id")
        private Long id;

        @Schema(description = "소속 골프장")
        private String golfFieldName;

        @Schema(description = "이름")
        private String name;

        @Schema(description = "연락처")
        private String phoneNumber;

        @Schema(description = "조 이름")
        private String team;

        @Schema(description = "조 역할")
        private TeamRole teamRole;

        @Schema(description = "휴무일")
        private List<Days> holiday;

        @Schema(description = "변경 요청 휴무일")
        private List<Days> changedHoliday;

        @Schema(description = "참여하지않는 부")
        private List<Integer> offPart;

        @Schema(description = "성별")
        private Gender gender;

        @Schema(description = "생년월일")
        private LocalDate birth;

        @Schema(description = "주소")
        private String address;

        @Schema(description = "상세주소")
        private String addressDetail;

        @Schema(description = "경력")
        private String career;

        @Schema(description = "캐디 분류")
        private String caddyType;
    }
}
