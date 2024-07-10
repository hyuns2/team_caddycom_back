package com.flash21.caddycom.dto.golfFieldDetail.tee;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class TeeDto {
    @Getter
    @AllArgsConstructor
    public static class info {
        private Long id;
        private String name;
        private int distance;
    }
}
