package com.flash21.caddycom.dto.golfFieldDetail.formation;

import com.flash21.caddycom.dto.golfFieldDetail.course.CourseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class FormationResponse {

    @Getter
    @AllArgsConstructor
    public static class Info {
        private Long id;
        private String name;
        private List<CourseResponse.Info> courseInfos;
    }
}
