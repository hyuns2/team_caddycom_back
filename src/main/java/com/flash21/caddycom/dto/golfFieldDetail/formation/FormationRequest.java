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
        @NotNull(message = "golfFieldId는 필수값입니다.")
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
        @NotEmpty(message = "courseInfos는 필수값입니다.")
        private List<CourseRequest.Create> courseInfos;
    }
    @Getter
    @AllArgsConstructor
    public static class Update {
        @NotNull(message = "formationId는 필수값입니다.")
        private Long formationId;
        private String formationName;
        private List<CourseRequest.Update> courseInfos;
    }
}
