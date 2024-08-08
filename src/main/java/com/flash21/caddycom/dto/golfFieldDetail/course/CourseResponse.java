package com.flash21.caddycom.dto.golfFieldDetail.course;

import com.flash21.caddycom.dto.golfFieldDetail.hole.HoleResponse;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.golfFieldDetail.Hole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CourseResponse {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Info {
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

    @Getter
    @Builder
    @AllArgsConstructor
    public static class DetailMap {
        private Long courseId;
        private String courseName;
        private List<Integer> holeNumbers;
        private Map<Integer, HoleResponse.HoleInfo> holeDetail;

        public static DetailMap from(Course course) {
            return DetailMap.builder()
                    .courseName(course.getName())
                    .courseId(course.getId())
                    .holeNumbers(course.getHoles().stream()
                            .map(Hole::getNum)
                            .toList())
                    .holeDetail(course.getHoles().stream()
                            .collect(Collectors.toMap(
                                    Hole::getNum, // 홀 번호를 키로 사용
                                    HoleResponse.HoleInfo::from // 홀 정보를 값으로 사용
                            )))
                    .build();
        }
    }

}
