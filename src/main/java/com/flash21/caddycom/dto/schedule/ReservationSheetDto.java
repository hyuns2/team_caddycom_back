package com.flash21.caddycom.dto.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.schedule.DateStatus;
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
        @NotNull(message = "golfFieldId는 필수값입니다.")
        private Long golfFieldId;

        @Schema(description = "코스 리스트")
        @NotEmpty(message = "courseList는 필수값입니다.")
        private List<Long> courseList;

        @Schema(description = "시작날짜 (yyyy-mm-dd)")
        @NotNull(message = "startDate는 필수값입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate startDate;

        @Schema(description = "종료날짜 (yyyy-mm-dd)")
        @NotNull(message = "endDate는 필수값입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate endDate;

        @Schema(description = "시작시간 (hh:mm) 리스트")
        @NotEmpty(message = "startTimeList는 필수값입니다.")
        private List<String> startTimeList;

        @Schema(description = "종료시간 (hh:mm) 리스트")
        @NotEmpty(message = "endTimeList는 필수값입니다.")
        private List<String> endTimeList;

        @Schema(description = "티오프 리스트")
        @NotEmpty(message = "teeOffList는 필수값입니다.")
        private List<String> teeOffList;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class MetaDataResponse {
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
}
