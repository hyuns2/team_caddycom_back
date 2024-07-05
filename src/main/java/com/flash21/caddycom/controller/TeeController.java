package com.flash21.caddycom.controller;

import com.flash21.caddycom.dto.AllTeeSetRequest;
import com.flash21.caddycom.service.TeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "tee", description = "티 정보 관련 API")
@RestController
@RequiredArgsConstructor
public class TeeController {
    private final TeeService teeService;

    @PostMapping("/api/course/{courseId}/tees")
    @Operation(summary = "홀 전체 티 설정")
    public void setAllTees(@PathVariable Long courseId, @RequestBody AllTeeSetRequest request) {
        teeService.setAllTee(courseId, request);
    }
}
