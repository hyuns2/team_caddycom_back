package com.flash21.caddycom.dto.golfField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class GolfFieldResponse {
    @AllArgsConstructor
    @Getter
    @Builder
    public static class Overview{
        private String name;
        private String contact;

    }

}
