package com.flash21.caddycom.dto.golfFieldDetail.course;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class CourseDto {
    @Getter
    @AllArgsConstructor
    public static class info{
        private String name;
        private Integer totalHoles;
    }
}
