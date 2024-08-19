package com.flash21.caddycom.dto.schedule;

import com.flash21.caddycom.entity.schedule.DateStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservationSheetResponse {
    @Data
    @Builder
    public static class Get {
        private Long id;
        private List<CourseInfo> courseList;
        private LocalDate startDate;
        private LocalDate endDate;
        private List<InfoByPart> timeSlot;
    }

    @Data
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
    }

    @Data
    @Builder
    public static class CourseInfo {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    public static class InfoByPart {
        private LocalTime startTime;
        private LocalTime endTime;
        private String teeOff;
    }
}
