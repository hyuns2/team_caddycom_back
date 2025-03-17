package com.flash21.caddycom.dto.assignment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.flash21.caddycom.entity.caddy.Days;
import com.flash21.caddycom.entity.caddy.TeamRole;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.schedule.Assignment;
import com.flash21.caddycom.entity.schedule.AssignmentStatus;
import com.flash21.caddycom.entity.schedule.Schedule;
import lombok.*;

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
            Long caddyId = assignment.getCaddy() == null ? null : assignment.getCaddy().getId();

            return Info.builder()
                    .id(assignment.getId())
                    .startTime(assignment.getStartTime().format(timeFormatter))
                    .caddyName(assignment.getCaddyName())
                    .caddyId(caddyId)
                    .courseName(assignment.getSchedule().getCourse().getName())
                    .part(assignment.getSchedule().getPart())
                    .status(assignment.getAssignmentStatus())
                    .reason(assignment.getReason())
                    .build();
        }

        public static Info fromSwitchable(Assignment assignment) {
            return Info.builder()
                    .id(assignment.getId())
                    .startTime(assignment.getStartTime().format(timeFormatter))
                    .caddyName(assignment.getCaddyName())
                    .caddyId(assignment.getCaddy().getId())
                    .courseName(assignment.getSchedule().getCourse().getName())
                    .part(assignment.getSchedule().getPart())
                    .build();
        }
    }

    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    @Getter
    public static class CaddyAssignmentInfoDetail {

        private Long courseId;
        private Long assignmentId;
        private String golfFieldName;
        private LocalDate assignmentDate;
        private Days days;
        private String courseName;
        private int part;
        private int totalHole;
        private String startTime;


        public static CaddyAssignmentInfoDetail from(Assignment assignment) {

            Schedule schedule = assignment.getSchedule();
            GolfField golfField = schedule.getGolfField();
            Course course = schedule.getCourse();

            LocalDate reservationAt = assignment.getSchedule().getReservationAt();

            return CaddyAssignmentInfoDetail.builder()
                    .courseId(course.getId())
                    .assignmentId(assignment.getId())
                    .golfFieldName(golfField.getName())
                    .assignmentDate(reservationAt)
                    .days(Days.fromNumber(reservationAt.getDayOfWeek().getValue()))
                    .courseName(course.getName())
                    .part(schedule.getPart())
                    .totalHole(course.getTotalHoles())
                    .startTime(assignment.getStartTime().toString())
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
                    .caddyTeamRole(convertRole(assignment.getCaddy().getHouseCaddy().getTeamRole()))
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Block {
        private String caddyName;
        private String reason;

        //TODO: 서비스 레이어로 이동 필요
        public Block(Assignment assignment) {
            this.reason = assignment.getReason() != null ? assignment.getReason() : "사용자의 요청으로 블락된 상태입니다.";
            this.caddyName = assignment.getCaddyName() != null ? assignment.getCaddyName() : "블락 상태에서 캐디가 배정되지 않았습니다.";
        }

    }
}
