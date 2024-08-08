package com.flash21.caddycom.dto.assignment;

import lombok.AllArgsConstructor;
import lombok.Getter;


public class AssignmentRequest {
    @Getter
    @AllArgsConstructor
    public static class Cancel {
        private final String reason;
    }
}
