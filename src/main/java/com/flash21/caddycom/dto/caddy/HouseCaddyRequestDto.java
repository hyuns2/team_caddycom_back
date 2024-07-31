package com.flash21.caddycom.dto.caddy;

import com.flash21.caddycom.entity.caddy.Days;
import com.flash21.caddycom.entity.caddy.Gender;
import com.flash21.caddycom.entity.caddy.TeamRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

public class HouseCaddyRequestDto {
    @Getter
    @AllArgsConstructor
    public static class createHoliday {
        private Long id;
        private List<Days> holidays;
    }

    @Data
    public static class CaddySearchCond {

        private String team;
        private String name;
    }

    @Data
    public static class updateHouseCaddy {
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
}
