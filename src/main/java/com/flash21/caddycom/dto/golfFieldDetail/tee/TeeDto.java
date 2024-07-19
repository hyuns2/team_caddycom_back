package com.flash21.caddycom.dto.golfFieldDetail.tee;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class TeeDto {
    @Getter
    @AllArgsConstructor
    public static class Info {
        private Long id;
        private String name;
        private Integer distance;
    }
}
