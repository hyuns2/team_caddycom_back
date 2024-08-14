package com.flash21.caddycom.dto.golfFieldDetail.tee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class TeeResponse {
    @Getter
    @AllArgsConstructor
    @Setter
    @NoArgsConstructor
    public static class Info {
        private Long id;
        private String name;
        private Integer distance;
    }
}
