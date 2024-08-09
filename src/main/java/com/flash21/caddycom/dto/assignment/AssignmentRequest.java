package com.flash21.caddycom.dto.assignment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;


public class AssignmentRequest {
    @Getter
    @AllArgsConstructor
    public static class Cancel {
        private final String reason;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Start {
        @NotNull
        private Long courseId;
        @NotNull
        private LocalTime startedTime;

        public static Start from(Long courseId, LocalTime startedTime) {
            return Start.builder()
                    .courseId(courseId)
                    .startedTime(startedTime)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class End {
        @NotNull
        private LocalTime endedTime;

        public static End from(LocalTime endedTime) {
            return End.builder()
                    .endedTime(endedTime)
                    .build();
        }
    }

}
