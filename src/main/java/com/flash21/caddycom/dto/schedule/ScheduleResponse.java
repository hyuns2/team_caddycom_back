package com.flash21.caddycom.dto.schedule;

import com.flash21.caddycom.entity.schedule.Assignment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

//TODO: AssignmentResponse 로 이동 .??
public class ScheduleResponse {
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class NotAssigned {
        private LocalDate date;
        private Integer count;
        private List<NotAssignedItem> notAssignedItems;

        public static NotAssigned of(LocalDate date, Integer count, List<Assignment> assignments) {
            return NotAssigned.builder()
                    .date(date)
                    .count(count)
                    .notAssignedItems(assignments.stream()
                            .sorted(Comparator.comparing(Assignment::getStartTime))
                            .map(NotAssignedItem::from)
                            .toList())
                    .build();
        }

    }

    @AllArgsConstructor
    @Getter
    @Builder
    public static class NotAssignedItem {
        private Long assignmentId;
        private LocalTime startTime;
        private String courseName;
        private Integer totalHoles;
        private Integer part;

        public static NotAssignedItem from(Assignment assignment){
            return NotAssignedItem.builder()
                    .assignmentId(assignment.getId())
                    .startTime(assignment.getStartTime())
                    .courseName(assignment.getSchedule().getCourse().getName())
                    .totalHoles(assignment.getSchedule().getCourse().getTotalHoles())
                    .part(assignment.getSchedule().getPart())
                    .build();
        }
    }
}
