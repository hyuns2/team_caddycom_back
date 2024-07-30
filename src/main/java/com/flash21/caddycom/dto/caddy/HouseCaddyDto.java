package com.flash21.caddycom.dto.caddy;

import com.flash21.caddycom.entity.caddy.Days;
import com.flash21.caddycom.entity.caddy.Gender;
import com.flash21.caddycom.entity.caddy.TeamRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class HouseCaddyDto {
    @Data
    public static class updateHouseCaddyRequest {
        @Schema(description = "조 이름")
        private String team;

        @Schema(description = "LEADER/MEMBER")
        private TeamRole teamRole;

        @Schema(description = "휴무일 ex) [\"MON\", \"FRI\"]")
        private List<Days> holiday;

        @Schema(description = "성별")
        private Gender gender;

        @Schema(description = "생년월일")
        private String birth;

        @Schema(description = "주소")
        private String address;

        @Schema(description = "상세주소")
        private String addressDetail;

        @Schema(description = "경력")
        private String career;
    }
    @Data
    @AllArgsConstructor
    @Builder
    public static class houseCaddyResponse {
        @Schema(description = "캐디 Id")
        private Long id;

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
    }

    @Getter
    @AllArgsConstructor
    public static class HolidayInfo{
        private Long id;
        private String name;
        private String teamRole;
        private List<Days> holiday;
    }
}
