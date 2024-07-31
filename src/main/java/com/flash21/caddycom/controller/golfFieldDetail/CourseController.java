package com.flash21.caddycom.controller.golfFieldDetail;

import com.flash21.caddycom.dto.golfFieldDetail.course.CourseResponse;
import com.flash21.caddycom.service.golfFieldDetail.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "2-2. Course", description = "코스 API")
@RequestMapping("/api/course")
public class CourseController {
    final CourseService courseService;

    @Operation(summary = "코스 전체조회", description = "모든 코스정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<CourseResponse.Info>> retrieveCourseInfo() {
        List<CourseResponse.Info> responseDtoList = courseService.retrieveCourseInfo();

        return new ResponseEntity<>(responseDtoList, HttpStatus.OK);
    }

    @GetMapping("/details/{formationId}")
    @Operation(summary = "코스의 모든 홀 정보 조회 API")
    public ResponseEntity<List<CourseResponse.Detail>> getHoles(@PathVariable Long formationId) {
        List<CourseResponse.Detail> holeInfos = courseService.getHoles(formationId);
        return new ResponseEntity<>(holeInfos, HttpStatus.OK);
    }
}
