package com.flash21.caddycom.dto.golfFieldDetail.tee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class TeeRequest {
    @Getter
    @AllArgsConstructor
    public static class Create {
        @NotBlank(message = "name은 필수값입니다.")
        private String name;
        @NotNull(message = "distance는 필수값입니다.")
        private Integer distance;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateAll {
        private List<Create> createInfos;
    }
}
