package com.flash21.caddycom.dto.golfFieldDetail.formation;

import com.flash21.caddycom.dto.golfFieldDetail.course.CourseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class FormationRequest {
    @Getter
    @AllArgsConstructor
    public static class create {
        @NotBlank
        private String name;
        @NotEmpty
        private List<CourseDto.info> courseInfos;
    }
}
