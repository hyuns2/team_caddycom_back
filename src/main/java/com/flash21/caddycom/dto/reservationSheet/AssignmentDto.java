package com.flash21.caddycom.dto.reservationSheet;

import com.flash21.caddycom.entity.reservationSheet.AssignmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class AssignmentDto {
    @Data
    @AllArgsConstructor
    @Builder
    public static class AssignmentsResponse {
        @Schema(description = "배정정보 id")
        private Long id;

        @Schema(description = "상태")
        private AssignmentStatus status;
    }
}
