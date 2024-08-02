package com.flash21.caddycom.dto.schedule;

import com.flash21.caddycom.entity.schedule.Assignment;
import com.flash21.caddycom.entity.schedule.AssignmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
        private String reason;

        public BlockResponse(Assignment assignment) {
            if (assignment.getReason() != null) {
                this.reason = assignment.getReason();
            } else {
                this.reason = "사용자의 요청으로 블락되었습니다.";
            }
        }
    }
}
