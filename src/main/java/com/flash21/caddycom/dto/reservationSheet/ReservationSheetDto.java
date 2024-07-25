package com.flash21.caddycom.dto.reservationSheet;

import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.reservationSheet.ReservationSheet;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationSheetDto {

    @Data
    @AllArgsConstructor
    public static class CreateRequest {
        @Schema(description = "골프장 Id")
        @NotNull
        private Long golfFieldId;

        @Schema(description = "코스 리스트")
        @NotNull
        private List<Long> courseList;

        @Schema(description = "시작 날짜 (yyyy-mm-dd)")
        @NotNull
        private LocalDate startDate;

        @Schema(description = "종료 날짜 (yyyy-mm-dd)")
        @NotNull
        private LocalDate endDate;

        @Schema(description = "시작시간 (hh:mm) 리스트")
        @NotEmpty
        private List<String> startTimeList;

        @Schema(description = "종료시간 (hh:mm) 리스트")
        @NotEmpty
        private List<String> endTimeList;

        @Schema(description = "티오프 리스트")
        @NotEmpty
        private List<String> teeOffList;

        public static List<ReservationSheet> toEntities(GolfField goldField, CreateRequest dto, List<Course> courseList) {
            List<ReservationSheet> sheets = new ArrayList<>();
            int part = 1;

            for (int i = 0; i < dto.teeOffList.size(); i++) {
                for (Course course: courseList) {
                    ReservationSheet sheet = ReservationSheet.builder().
                            golfField(goldField).
                            course(course).
                            startDateTime(LocalDateTime.of(dto.getStartDate(), LocalTime.parse(dto.startTimeList.get(i)))).
                            endDateTime(LocalDateTime.of(dto.getEndDate(),LocalTime.parse(dto.endTimeList.get(i)))).
                            teeOff(dto.teeOffList.get(i)).
                            part(part).build();
                    sheets.add(sheet);
                }
                part++;
            }
            return sheets;
        }
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class MetaDataResponse {
        @Schema(description = "결과 날짜 (yyyy-mm-dd)")
        private LocalDate targetDate;

        @Schema(description = "총 개수")
        private int totalCntSum;

        @Schema(description = "블락된 개수")
        private int blockedCntSum;

        @Schema(description = "배정가능 개수")
        private int availableCntSum;
    }
}
