package com.flash21.caddycom.dto.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.schedule.ReservationSheet;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservationSheetRequest {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class CreateOrUpdate {
        @Schema(description = "골프장 Id")
        @NotNull(message = "golfFieldId는 필수값입니다.")
        private Long golfFieldId;

        @Schema(description = "코스 Id 리스트")
        @NotEmpty(message = "courseIds는 필수값입니다.")
        private List<Long> courseIds;

        @Schema(description = "시작날짜 (yyyy-mm-dd)")
        @NotNull(message = "startDate는 필수값입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate startDate;

        @Schema(description = "종료날짜 (yyyy-mm-dd)")
        @NotNull(message = "endDate는 필수값입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate endDate;

        @Schema(description = "시작시간 (hh:mm) 리스트")
        @NotEmpty(message = "startTimes는 필수값입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
        private List<LocalTime> startTimes;

        @Schema(description = "종료시간 (hh:mm) 리스트")
        @NotEmpty(message = "endTimes는 필수값입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
        private List<LocalTime> endTimes;

        @Schema(description = "티오프 리스트")
        @NotEmpty(message = "teeOffs는 필수값입니다.")
        private List<String> teeOffs;

        public static CreateOrUpdate of(Long golfFieldId, List<Long> courseIdList, LocalDate startDate, LocalDate endDate,
                                 List<LocalTime> startTimes, List<LocalTime> endTimes, List<String> teeOffs) {
            return CreateOrUpdate.builder()
                    .golfFieldId(golfFieldId)
                    .courseIds(courseIdList)
                    .startDate(startDate)
                    .endDate(endDate)
                    .startTimes(startTimes)
                    .endTimes(endTimes)
                    .teeOffs(teeOffs).build();
        }

        public ReservationSheet toEntity(GolfField golfField) {
            return ReservationSheet.builder()
                    .golfField(golfField)
                    .courseIds(courseIds)
                    .startDate(startDate)
                    .endDate(endDate)
                    .startTimes(startTimes)
                    .endTimes(endTimes)
                    .teeOffs(teeOffs)
                    .build();
        }
    }

}
