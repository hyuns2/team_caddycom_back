package com.flash21.caddycom.dto.caddy;

import com.flash21.caddycom.entity.caddy.Days;
import com.flash21.caddycom.entity.caddy.Gender;
import com.flash21.caddycom.entity.caddy.TeamRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

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
    public static class updateHouseCaddyByManager {
        @NotNull
        @Schema(description = "조 이름")
        private String team;

        @NotNull
        @Schema(description = "LEADER/MEMBER")
        private TeamRole teamRole;

        @NotNull
        @Schema(description = "휴무일 ex) [\"MON\", \"FRI\"]")
        private List<Days> holiday;

        @NotNull
        @Schema(description = "참여하지않는 부")
        private List<Integer> offPart;

        @NotNull
        @Schema(description = "성별")
        private Gender gender;

        @NotNull
        @Schema(description = "생년월일")
        private String birth;

        @NotNull
        @Schema(description = "주소")
        private String address;

        @NotNull
        @Schema(description = "상세주소")
        private String addressDetail;

        @NotNull
        @Schema(description = "경력")
        private String career;
    }

    @Data
    public static class updateHouseCaddy {
        @Schema(description = "프로필 사진파일")
        private MultipartFile profile;

        @Schema(description = "변경을 희망하는 휴무일 ex) [\"MON\", \"FRI\"]")
        private List<Days> changedHoliday;

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
