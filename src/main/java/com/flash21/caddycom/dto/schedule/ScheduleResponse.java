package com.flash21.caddycom.dto.schedule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.flash21.caddycom.entity.schedule.Assignment;
import com.flash21.caddycom.entity.schedule.AssignmentStatus;
import com.flash21.caddycom.entity.schedule.DateStatus;
import com.flash21.caddycom.repository.schedule.MetaDataReport;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ScheduleResponse {
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @Getter
    @Builder
    public static class MetaData {
        @Schema(description = "결과 날짜 (yyyy-mm-dd)")
        private LocalDate targetDate;

        @Schema(description = "일별 배정상태")
        private DateStatus dateStatus;

        @Schema(description = "총 개수")
        private int totalCntSum;

        @Schema(description = "블락된 개수")
        private int blockedCntSum;

        @Schema(description = "배정가능 개수")
        private int availableCntSum;

        public static MetaData from(MetaDataReport report){
            return MetaData.builder()
                    .targetDate(report.getReservationAt())
                    .dateStatus(report.getDateStatus())
                    .totalCntSum(report.getTotalCntSum())
                    .blockedCntSum(report.getBlockedCntSum())
                    .availableCntSum(report.getDateStatus() != DateStatus.NOTHING ? report.getTotalCntSum() - report.getBlockedCntSum() : 0)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class AssignmentInfo {
        @Schema(description = "배정정보 id")
        private Long id;

        @Schema(description = "상태")
        private AssignmentStatus status;

        public static AssignmentInfo from(Assignment assignment) {
            if (assignment == null)
                return null;

            return AssignmentInfo.builder()
                    .id(assignment.getId())
                    .status(assignment.getAssignmentStatus())
                    .build();
        }
    }

    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CaddyAssignmentInfo {

        private Long assignmentId;
        private Integer part;
        private String startTime;
        private String golfFieldName;
        @JsonIgnore
        private LocalDate date;

        @Builder
        public CaddyAssignmentInfo(Assignment assignment) {
            this.assignmentId = assignment.getId();
            this.part = assignment.getSchedule().getPart();
            this.date = assignment.getSchedule().getReservationAt();
            this.golfFieldName = assignment.getSchedule().getGolfField().getName();
            this.startTime = assignment.getStartTime().format(timeFormatter);
        }
    }
}
