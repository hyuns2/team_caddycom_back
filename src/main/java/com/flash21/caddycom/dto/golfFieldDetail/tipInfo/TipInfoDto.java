package com.flash21.caddycom.dto.golfFieldDetail.tipInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class TipInfoDto {
    @Getter
    @AllArgsConstructor
    public static class info {
        private Long id;
        private String title;
        private String content;
    }
}
