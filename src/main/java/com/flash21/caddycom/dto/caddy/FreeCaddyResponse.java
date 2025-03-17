package com.flash21.caddycom.dto.caddy;

import com.flash21.caddycom.entity.caddy.FreeCaddy;
import com.flash21.caddycom.entity.caddy.Gender;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.schedule.Assignment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

public class FreeCaddyResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Info {
        private String name;
        private String profileUrl;
        private Long point;
        private String regions;
        private Gender gender;
        private LocalDate birth;
        private String career;
        private String intro;
        private List<MatchedGolfFieldInfo> golfFieldList;

        public static Info from(FreeCaddy freeCaddy) {
            return Info.builder()
                    .name(freeCaddy.getName())
                    .profileUrl(freeCaddy.getProfileUrl())
                    .point(freeCaddy.getPoint())
                    .regions(freeCaddy.getRegions())
                    .gender(freeCaddy.getGender())
                    .birth(freeCaddy.getBirth())
                    .career(freeCaddy.getCareer())
                    .intro(freeCaddy.getIntro())
                    .golfFieldList(freeCaddy.getMatchedFreeCaddyList().stream()
                            .map(matchedFreeCaddy -> MatchedGolfFieldInfo.from(matchedFreeCaddy.getGolfField()))
                            .toList())
                    .build();

        }
    }


    @Getter
    @Builder
    @AllArgsConstructor
    private static class MatchedGolfFieldInfo {
        private String name;
        private String imageUrl;

        public static MatchedGolfFieldInfo from(GolfField golfField) {
            return MatchedGolfFieldInfo.builder()
                    .name(golfField.getName())
                    .imageUrl(golfField.getImageUrl())
                    .build();
        }
    }

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
