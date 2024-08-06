package com.flash21.caddycom.dto.golfField;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class FacilityRequest {
    @Getter
    @AllArgsConstructor
    public static class Create {
        @NotBlank(message = "name은 필수값입니다.")
        private final String name;

        private final String content;

        private final List<MultipartFile> facilityImages = new ArrayList<>();
    }

    @Getter
    @AllArgsConstructor
    public static class Update {
        @NotNull(message = "id는 필수값입니다.")
        private final Long facilityId;

        @NotBlank(message = "name은 필수값입니다.")
        private final String name;

        private final String content;

        private final List<Long> existingImageIds = new ArrayList<>();

        private final List<MultipartFile> facilityImages = new ArrayList<>();
    }
}
