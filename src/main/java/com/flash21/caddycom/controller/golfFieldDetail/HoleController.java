package com.flash21.caddycom.controller.golfFieldDetail;

import com.flash21.caddycom.dto.golfFieldDetail.hole.HoleRequest;
import com.flash21.caddycom.service.golfFieldDetail.HoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="2-3. Hole", description = "홀 정보 관련 API")
@RestController
@RequiredArgsConstructor
public class HoleController {
    private final HoleService holeService;

    @PatchMapping("/api/hole/handicap")
    @Operation(summary = "홀의 핸디 수 변경 API")
    public void updateHandicap(@Valid  @RequestBody HoleRequest.updateHandicap request) {
        holeService.updateHandicap(request);
    }

    @PatchMapping("/api/hole/par")
    @Operation(summary = "홀의 파 변경 API")
    public void updatePar(@Valid @RequestBody HoleRequest.updatePar request) {
        holeService.updatePar(request);
    }

    @PostMapping("/api/hole/detail")
    @Operation(summary = "홀의 상세 정보 설정 API")
    public void createDetailInfo(@Valid @RequestBody HoleRequest.createDetailInfo request) {
        holeService.createDetailInfo(request);
    }
}
