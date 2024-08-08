package com.flash21.caddycom.dto.golfFieldDetail.course;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class CourseRequest {
    @Getter
    @AllArgsConstructor
    public static class Create {
        @NotNull(message = "name은 필수값입니다.")
        private final String name;
        @NotNull(message = "totalHoles은 필수값입니다.")
        private final Integer totalHoles;
    }

    @Getter
    @AllArgsConstructor
    public static class Update {
        @NotNull(message = "id는 필수값입니다.")
        private final Long id;
        @NotNull(message = "name은 필수값입니다.")
        private final String name;
        @NotNull(message = "totalHoles은 필수값입니다.")
        private final Integer totalHoles;
    }
}
