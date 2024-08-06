package com.flash21.caddycom.dto.assignment;

import lombok.Getter;


public class AssignmentRequest {
    @Getter
    public static class Cancel {
        private String reason;
    }
}
