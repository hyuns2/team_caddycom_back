package com.flash21.caddycom.dto.assignment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.flash21.caddycom.entity.caddy.Days;
import com.flash21.caddycom.entity.caddy.TeamRole;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.schedule.Assignment;
import com.flash21.caddycom.entity.schedule.AssignmentStatus;
import com.flash21.caddycom.entity.schedule.Schedule;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.DayOfWeek;
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
                    .status(assignment.getStatus())
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
                    .days(getDayOfWeek(reservationAt))
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

    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    @Getter
    public static class CaddyAssignmentInfo {

        private Long assignmentId;
        private Integer part;
        private String startTime;
        private String golfFieldName;
        @JsonIgnore
        private LocalDate date;

        public CaddyAssignmentInfo(Assignment assignment) {
            this.assignmentId = assignment.getId();
            this.part = assignment.getSchedule().getPart();
            this.date = assignment.getSchedule().getReservationAt();
            this.golfFieldName = assignment.getSchedule().getGolfField().getName();
            this.startTime = assignment.getStartTime().format(timeFormatter);
        }
    }

    private static Days getDayOfWeek(LocalDate date) {
        int dayOfWeek = getDayofWeekFromRequestDate(date);
        return Days.fromNumber(String.valueOf(dayOfWeek));
    }

    private static int getDayofWeekFromRequestDate(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return switch (dayOfWeek) {
            case MONDAY -> 1;
            case TUESDAY -> 2;
            case WEDNESDAY -> 3;
            case THURSDAY -> 4;
            case FRIDAY -> 5;
            case SATURDAY -> 6;
            case SUNDAY -> 7;
        };
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Block {
        private String caddyName;
        private String reason;

        public Block(Assignment assignment) {
            this.reason = assignment.getReason() != null ? assignment.getReason() : "사용자의 요청으로 블락된 상태입니다.";
            this.caddyName = assignment.getCaddyName() != null ? assignment.getCaddyName() : "블락 상태에서 캐디가 배정되지 않았습니다.";
        }

    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Assigned {
        @NotNull(message = "id는 필수값입니다.")
        @Schema(description = "배정정보 id")
        private Long id;

        @Schema(description = "상태")
        private AssignmentStatus status;
    }
}
