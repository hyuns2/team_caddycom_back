package com.flash21.caddycom.dto.golfField;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FacilityRequest {
    @Getter
    @AllArgsConstructor
    public static class Create {
        @NotBlank(message = "name은 필수값입니다.")
        private String name;

        private String content;

        private List<MultipartFile> facilityImages;
    }

    @Getter
    @AllArgsConstructor
    public static class Update {
        @NotNull(message = "id는 필수값입니다.")
        private Long facilityId;

        @NotBlank(message = "name은 필수값입니다.")
        private String name;

        private String content;

        private List<Long> existingImageIds;

        private List<MultipartFile> facilityImages;
    }
}
