package com.flash21.caddycom.dto.golfFieldDetail.formation;

import com.flash21.caddycom.dto.golfFieldDetail.course.CourseRequest;
import com.flash21.caddycom.dto.golfFieldDetail.course.CourseResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class FormationResponse {

    @Getter
    @AllArgsConstructor
    public static class create {
        private Long id;
        private String name;
        private List<CourseResponse.create> courseInfos;
    }
}
