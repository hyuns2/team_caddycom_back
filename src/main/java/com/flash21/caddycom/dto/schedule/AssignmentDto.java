package com.flash21.caddycom.dto.schedule;

import com.flash21.caddycom.entity.schedule.Assignment;
import com.flash21.caddycom.entity.schedule.AssignmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AssignmentDto {
    @Data
    @AllArgsConstructor
    @Builder
    public static class AssignmentsResponse {
        @NotNull(message = "id는 필수값입니다.")
        @Schema(description = "배정정보 id")
        private Long id;

        @Schema(description = "상태")
        private AssignmentStatus status;
    }


    // TODO: AssignmentRequest 에 동일한 dto 존재
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BlockRequest {
        private String reason;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BlockResponse {
        private String caddyName;
        private String reason;

        public BlockResponse(Assignment assignment) {
            this.reason = assignment.getReason() != null ? assignment.getReason() : "사용자의 요청으로 블락된 상태입니다.";
            this.caddyName = assignment.getCaddyName() != null ? assignment.getCaddyName() : "블락 상태에서 캐디가 배정되지 않았습니다.";
        }

    }
}
