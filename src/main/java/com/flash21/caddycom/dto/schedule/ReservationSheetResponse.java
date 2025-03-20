package com.flash21.caddycom.dto.schedule;

import com.flash21.caddycom.entity.schedule.ReservationSheet;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class ReservationSheetResponse {
    @Getter
    @Builder
    public static class Get {
        @Schema(description = "예약시트 id")
        private Long id;

        @Schema(description = "코스id-코스이름 형식의 맵")
        private Map<Long, String> courses;

        @Schema(description = "시작날짜 (yyyy-mm-dd)")
        private LocalDate startDate;

        @Schema(description = "종료날짜 (yyyy-mm-dd)")
        private LocalDate endDate;

        @Schema(description = "시작시간 (hh:mm) 리스트")
        private List<LocalTime> startTimes;

        @Schema(description = "종료시간 (hh:mm) 리스트")
        private List<LocalTime> endTimes;

        @Schema(description = "티오프 리스트")
        private List<String> teeOffs;

        public static Get from(ReservationSheet reservationSheet, Map<Long, String> courseMap) {
            return Get.builder()
                    .id(reservationSheet.getId())
                    .courses(courseMap)
                    .startDate(reservationSheet.getStartDate())
                    .endDate(reservationSheet.getEndDate())
                    .startTimes(reservationSheet.getStartTimes())
                    .endTimes(reservationSheet.getEndTimes())
                    .teeOffs(reservationSheet.getTeeOffs()).build();
        }
    }
}
