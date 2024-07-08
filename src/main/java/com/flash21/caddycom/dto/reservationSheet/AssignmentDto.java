package com.flash21.caddycom.dto.reservationSheet;

import com.flash21.caddycom.entity.reservationSheet.AssignmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

public class AssignmentDto {
    @Data
    @AllArgsConstructor
    @Builder
    public static class AssignmentsResponseDto {
        @Schema(description = "배정정보 id")
        private Long id;

        @Schema(description = "예약날짜 (yyyy-mm-dd)")
        private LocalDate reservationDate;

        @Schema(description = "시작시간")
        private LocalTime startTime;

        @Schema(description = "상태")
        private AssignmentStatus status;

//        @Schema(description = "caddy id")
//        private Long caddyId;

        @Schema(description = "caddy 이름")
        private String caddyName;

        @Schema(description = "사유")
        private String reason;
    }
}
