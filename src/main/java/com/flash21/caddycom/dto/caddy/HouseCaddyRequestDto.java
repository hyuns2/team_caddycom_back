package com.flash21.caddycom.dto.caddy;

import com.flash21.caddycom.entity.caddy.Days;
import com.flash21.caddycom.entity.caddy.Gender;
import com.flash21.caddycom.entity.caddy.TeamRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class HouseCaddyRequestDto {
    @Getter
    @AllArgsConstructor
    public static class createHoliday {
        @NotNull(message = "id는 필수값입니다.")
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
        @NotNull(message = "team는 필수값입니다.")
        @Schema(description = "조 이름")
        private String team;

        @NotNull(message = "teamRole는 필수값입니다.")
        @Schema(description = "LEADER/MEMBER")
        private TeamRole teamRole;

        @NotNull(message = "holiday은 필수값입니다.")
        @Schema(description = "휴무일 ex) [\"MON\", \"FRI\"]")
        private List<Days> holiday;

        @NotNull(message = "offPart는 필수값입니다.")
        @Schema(description = "참여하지않는 부")
        private List<Integer> offPart;

        @NotNull(message = "gender은 필수값입니다.")
        @Schema(description = "성별")
        private Gender gender;

        @NotNull(message = "birth은 필수값입니다.")
        @Schema(description = "생년월일")
        private String birth;

        @NotNull(message = "address는 필수값입니다.")
        @Schema(description = "주소")
        private String address;

        @NotNull(message = "addressDetail은 필수값입니다.")
        @Schema(description = "상세주소")
        private String addressDetail;

        @NotNull(message = "career는 필수값입니다.")
        @Schema(description = "경력")
        private String career;
    }

    @Data
    @AllArgsConstructor
    public static class updateHouseCaddy {
        @Schema(description = "프로필 사진파일")
        private final MultipartFile profile = null;

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
