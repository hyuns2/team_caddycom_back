package com.flash21.caddycom.dto.golfFieldDetail.course;

import com.flash21.caddycom.dto.golfFieldDetail.hole.HoleResponse;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class CourseResponse {
    @Getter
    @AllArgsConstructor
    public static class Create {
        private Long id;
        private String name;
        private Integer totalHoles;
    }


    @Getter
    @Builder
    @AllArgsConstructor
    public static class Detail {
        private Long courseId;
        private List<HoleResponse.HoleInfo> holeInfos;

        public static Detail from(Course course) {
            return Detail.builder()
                    .courseId(course.getId())
                    .holeInfos(course.getHoles().stream()
                            .map(HoleResponse.HoleInfo::from)
                            .toList())
                    .build();
        }
    }
}
