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
    public static class create {
        @NotBlank
        private String name;
        @NotNull
        private Integer distance;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class createAll {
        private List<create> createInfos;
    }
}
