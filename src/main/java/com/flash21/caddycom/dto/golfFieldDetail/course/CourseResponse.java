package com.flash21.caddycom.dto.golfFieldDetail.course;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class CourseResponse {
    @Getter
    @AllArgsConstructor
    public static class create {
        private String name;
        private Integer totalHoles;
    }
}
