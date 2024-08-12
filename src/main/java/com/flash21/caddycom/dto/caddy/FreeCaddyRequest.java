package com.flash21.caddycom.dto.caddy;

import com.flash21.caddycom.entity.caddy.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;


public class FreeCaddyRequest {

    @Getter
    @AllArgsConstructor
    public static class Create {
        @NotBlank(message = "name은 필수입니다.")
        @Schema(example = "이름(String)")
        private String name;

        @NotBlank(message = "phoneNumber은 필수입니다.")
        @Schema(example = "전화번호(010-5555-5555)")
        private String phoneNumber;

        @NotBlank(message = "regions은 필수입니다.")
        @Schema(example = "활동지역(String)")
        private String regions;

        @NotNull(message = "gender은 필수입니다.")
        private Gender gender;

        @NotNull(message = "birth은 필수입니다.")
        @Schema(example = "생년월일(yyyy-MM-dd)")
        private LocalDate birth;

        @NotBlank(message = "career은 필수입니다.")
        @Schema(example = "경력(String)")
        private String career;

        private String intro;

        private List<Long> golfFieldIdList;

        private MultipartFile profileUrl;

    }
}
