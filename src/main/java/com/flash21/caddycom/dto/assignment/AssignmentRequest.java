package com.flash21.caddycom.dto.assignment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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
    public static class End {
        private LocalTime endedTime;

        public static End from(LocalTime endedTime) {
            return End.builder()
                    .endedTime(endedTime)
                    .build();
        }
    }
}
