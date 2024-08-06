package com.flash21.caddycom.dto.assignment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.flash21.caddycom.entity.caddy.TeamRole;
import com.flash21.caddycom.entity.schedule.Assignment;
import com.flash21.caddycom.entity.schedule.AssignmentStatus;
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
            Long caddyId = assignment.getHouseCaddy() == null ? null : assignment.getHouseCaddy().getId();

            return Info.builder()
                    .id(assignment.getId())
                    .startTime(assignment.getStartTime().format(timeFormatter))
                    .caddyName(assignment.getCaddyName())
                    .caddyId(caddyId)
                    .courseName(assignment.getSchedule().getCourse().getName())
                    .part(assignment.getSchedule().getPart())
                    .status(assignment.getStatus())
                    .reason(assignment.getReason())
                    .build();
        }

        public static Info fromSwitchable(Assignment assignment) {
            return Info.builder()
                    .id(assignment.getId())
                    .startTime(assignment.getStartTime().format(timeFormatter))
                    .caddyName(assignment.getCaddyName())
                    .caddyId(assignment.getHouseCaddy().getId())
                    .courseName(assignment.getSchedule().getCourse().getName())
                    .part(assignment.getSchedule().getPart())
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
                    .caddyName(assignment.getHouseCaddy().getName())
                    .caddyPoint(assignment.getHouseCaddy().getPoint())
                    .caddyTeamRole(convertRole(assignment.getHouseCaddy().getTeamRole()))
                    .golfFieldName(assignment.getSchedule().getGolfField().getName())
                    .date(assignment.getSchedule().getReservationAt())
                    .courseName(assignment.getSchedule().getCourse().getName())
                    .part(assignment.getSchedule().getPart())
                    .totalHole(assignment.getSchedule().getCourse().getTotalHoles())
                    .startTime(assignment.getStartTime().format(timeFormatter))
                    .reason(assignment.getReason())
                    .build();
        }

        private static String convertRole(TeamRole role) {
            if (role == TeamRole.MEMBER)
                return "조원";
            return "조장";

        }
    }

    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    @Getter
    public static class CaddyAssignmentInfo {

        private Long assignmentId;
        private Integer part;
        private String startTime;
        @JsonIgnore
        private LocalDate date;

        public CaddyAssignmentInfo(Assignment assignment) {
            this.assignmentId = assignment.getId();
            this.part = assignment.getSchedule().getPart();
            this.date = assignment.getSchedule().getReservationAt();
            this.startTime = assignment.getStartTime().format(timeFormatter);
        }
    }
}
