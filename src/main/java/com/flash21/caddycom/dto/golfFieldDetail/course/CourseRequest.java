package com.flash21.caddycom.dto.golfFieldDetail.course;


import lombok.AllArgsConstructor;
import lombok.Getter;

public class CourseRequest {
    @Getter
    @AllArgsConstructor
    public static class create {
        private String name;
        private Integer totalHoles;
    }

    @Getter
    @AllArgsConstructor
    public static class update {
        private Long id;
        private String name;
        private Integer totalHoles;
    }
}
