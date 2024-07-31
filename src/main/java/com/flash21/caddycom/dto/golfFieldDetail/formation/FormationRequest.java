package com.flash21.caddycom.dto.golfFieldDetail.formation;

import com.flash21.caddycom.dto.golfFieldDetail.course.CourseRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class FormationRequest {
    @Getter
    @AllArgsConstructor
    public static class process {
        @NotNull
        private Long golfFieldId;
        private List<create> create;
        private List<update> update;
        private List<Long> deleteFormations;
        private List<Long> deleteCourses;
    }
    @Getter
    @AllArgsConstructor
    public static class create {
        private String name;
        @NotEmpty
        private List<CourseRequest.create> courseInfos;
    }
    @Getter
    @AllArgsConstructor
    public static class update {
        private Long formationId;
        private String formationName;
        private List<CourseRequest.update> courseInfos;
    }
}
