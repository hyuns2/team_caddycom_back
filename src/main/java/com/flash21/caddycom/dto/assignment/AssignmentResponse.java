package com.flash21.caddycom.dto.assignment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.flash21.caddycom.entity.reservationSheet.AssignmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class AssignmentResponse {

    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    @Getter
    public static class Info {
        private Long id;
        private String startTime;
        private String caddyName;
        private String courseName;
        private String part;
        private AssignmentStatus status;
        private String reason;
    }
}
