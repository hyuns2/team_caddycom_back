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
    public static class Process {
        @NotNull
        private Long golfFieldId;
        private List<Create> create;
        private List<Update> update;
        private List<Long> deleteFormations;
        private List<Long> deleteCourses;
    }
    @Getter
    @AllArgsConstructor
    public static class Create {
        private String name;
        @NotEmpty
        private List<CourseRequest.create> courseInfos;
    }
    @Getter
    @AllArgsConstructor
    public static class Update {
        private Long formationId;
        private String formationName;
        private List<CourseRequest.update> courseInfos;
    }
}
