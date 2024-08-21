package com.flash21.caddycom.dto.schedule;

import com.flash21.caddycom.entity.schedule.DateStatus;
import com.flash21.caddycom.entity.schedule.Schedule;
import com.flash21.caddycom.repository.schedule.MetaDataReport;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservationSheetResponse {
    @Data
    @Builder
    @AllArgsConstructor
    public static class Get {
        private Long id;
        private List<CourseInfo> courseList;
        private LocalDate startDate;
        private LocalDate endDate;
        private List<InfoByPart> timeSlot;
    }

    @Data
    @Builder
    @AllArgsConstructor
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
            int availableCntResult = report.getTotalCntSum() - report.getBlockedCntSum();
            return MetaData.builder()
                    .targetDate(report.getReservationAt())
                    .dateStatus(report.getDateStatus())
                    .totalCntSum(report.getTotalCntSum())
                    .blockedCntSum(report.getBlockedCntSum())
                    .availableCntSum(report.getDateStatus() != DateStatus.NOTHING ? availableCntResult : 0)
                    .build();
        }
    }

    @Data
    @Builder
    public static class CourseInfo {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class InfoByPart {
        private LocalTime startTime;
        private LocalTime endTime;
        private String teeOff;

        public static InfoByPart from(Schedule schedule) {
            return InfoByPart.builder()
                    .startTime(schedule.getStartTime())
                    .endTime(schedule.getEndTime())
                    .teeOff(schedule.getTeeOff())
                    .build();
        }
    }
}
