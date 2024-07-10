package com.flash21.caddycom.dto.golfFieldDetail.tee;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class TeeRequest {
    @Getter
    @AllArgsConstructor
    public static class create {
        private String name;
        private int distance;
    }

    @Getter
    @AllArgsConstructor
    public static class createAll {
        private List<create> createInfos;
    }
}
