package com.flash21.caddycom.controller.golfFieldDetail;

import com.flash21.caddycom.dto.golfFieldDetail.course.CourseRequest;
import com.flash21.caddycom.dto.golfFieldDetail.course.CourseResponse;
import com.flash21.caddycom.service.golfFieldDetail.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "2-1. Course", description = "골프장 코스 정보(구성, 코스) 관련 API")
@RequestMapping("/api/course")
public class CourseController {
    final CourseService courseService;

    @Operation(summary = "골프장의 모든 코스 조회", description = "해당하는 골프장의 모든 코스정보를 조회합니다.")
    @GetMapping("/{golfFieldId}")
    public ResponseEntity<List<CourseResponse.Info>> retrieveCourseInfo(@PathVariable Long golfFieldId) {
        List<CourseResponse.Info> responseDtoList = courseService.retrieveCourseInfo(golfFieldId);

        return new ResponseEntity<>(responseDtoList, HttpStatus.OK);
    }

    @GetMapping("/details/{formationId}")
    @Operation(summary = "코스의 모든 홀 정보 조회 API")
    public ResponseEntity<List<CourseResponse.Detail>> getHoles(@PathVariable Long formationId) {
        List<CourseResponse.Detail> holeInfos = courseService.getHoles(formationId);
        return new ResponseEntity<>(holeInfos, HttpStatus.OK);
    }

    @DeleteMapping
    @Operation(summary = "코스 삭제 API")
    public ResponseEntity<Void> deleteCourse(@Valid @RequestBody CourseRequest.Delete request) {
        courseService.deleteCourses(request.getDeleteCourses());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
