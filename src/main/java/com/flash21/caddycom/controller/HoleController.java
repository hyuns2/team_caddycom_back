package com.flash21.caddycom.controller;

import com.flash21.caddycom.dto.hole.HandicapUpdate;
import com.flash21.caddycom.dto.hole.ParUpdate;
import com.flash21.caddycom.dto.hole.SaveHoleDetail;
import com.flash21.caddycom.service.HoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="hole", description = "홀 정보 관련 API")
@RestController
@RequiredArgsConstructor
public class HoleController {
    private final HoleService holeService;

    @PatchMapping("/api/hole/handicap")
    @Operation(summary = "홀의 핸디 수 변경 API")
    public void updateHandicap(@RequestBody HandicapUpdate request) {
        holeService.updateHandicap(request);
    }

    @PatchMapping("/api/hole/par")
    @Operation(summary = "홀의 파 변경 API")
    public void updatePar(@RequestBody ParUpdate request) {
        holeService.updatePar(request);
    }

    @PostMapping("/api/hole/detail")
    @Operation(summary = "홀의 상세 정보 설정 API")
    public void processDetailInfo(@RequestBody SaveHoleDetail request) {
        holeService.processDetailInfo(request);
    }
}
