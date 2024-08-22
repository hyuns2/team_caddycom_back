package com.flash21.caddycom.controller.golfFieldDetail;

import com.flash21.caddycom.dto.golfFieldDetail.tee.TeeRequest;
import com.flash21.caddycom.service.golfFieldDetail.TeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "2-2. Course Detail", description = "골프장 코스 상세 정보(홀,티) 관련 API")
@RestController
@RequiredArgsConstructor
public class TeeController {
    private final TeeService teeService;

    @PostMapping("/api/course/{courseId}/tees")
    @Operation(summary = "홀 전체 티 설정", description = """
            해당하는 코스에 존재하는 모든 홀의 티를 일괄적으로 설정한다.
            - 기존의 티를 모두 삭제한 후 새로 생성하는 방식으로 동작
            """)
    public void deleteAndCreateAllTees(@PathVariable Long courseId, @Valid @RequestBody TeeRequest.CreateAll request) {
        teeService.deleteAndCreateAllTee(courseId, request);
    }
}
