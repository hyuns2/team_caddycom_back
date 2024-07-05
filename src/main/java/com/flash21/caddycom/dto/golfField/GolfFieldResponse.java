package com.flash21.caddycom.dto.golfField;

import lombok.AllArgsConstructor;
import lombok.Builder;

public class GolfFieldResponse {
    @AllArgsConstructor
    @Builder
    public static class Overview{
        private String name;
        private String contact;

    }

}
