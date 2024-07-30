package com.flash21.caddycom.dto.assignment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.entity.reservationSheet.Assignment;
import com.flash21.caddycom.entity.reservationSheet.AssignmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AssignmentResponse {

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    @Getter
    public static class Info {
        private Long id;
        private String startTime;
        private String caddyName;
        private Long caddyId;
        private String courseName;
        private Integer part;
        private AssignmentStatus status;
        private String reason;

        public static Info from(Assignment assignment) {
            return Info.builder()
                    .id(assignment.getId())
                    .startTime(assignment.getStartTime().format(timeFormatter))
                    .caddyName(assignment.getCaddyName())
                    .caddyId(assignment.getId())
                    .courseName(assignment.getSchedule().getCourse().getName())
                    .part(assignment.getSchedule().getPart())
                    .status(assignment.getStatus())
                    .reason(assignment.getReason())
                    .build();
        }
    }


    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    @Getter
    public static class Detail {
        private String caddyName;
        private Long caddyPoint;
        private String caddyTeamRole;
        private String golfFieldName;
        private LocalDate date;
        private String courseName;
        private int part;
        private int totalHole;
        private String startTime;
        private String reason;


        public static Detail from(Assignment assignment) {
            return Detail.builder()
                    .caddyName(assignment.getCaddy().getName())
                    .caddyPoint(assignment.getCaddy().getPoint())
                    .caddyTeamRole(assignment.getCaddy().getTeamRole())
                    .golfFieldName(assignment.getSchedule().getGolfField().getName())
                    .date(assignment.getSchedule().getStartDateTime().toLocalDate())
                    .courseName(assignment.getSchedule().getCourse().getName())
                    .part(assignment.getSchedule().getPart())
                    .totalHole(assignment.getSchedule().getCourse().getTotalHoles())
                    .startTime(assignment.getStartTime().format(timeFormatter))
                    .reason(assignment.getReason())
                    .build();
        }
    }
}
