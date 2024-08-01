package com.flash21.caddycom.dto.assignment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class AssignmentRequest {
    @Getter
    @AllArgsConstructor
    public static class Cancel {
        private String reason;
    }
}
