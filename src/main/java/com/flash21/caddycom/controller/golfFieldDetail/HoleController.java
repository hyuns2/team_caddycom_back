package com.flash21.caddycom.controller.golfFieldDetail;

import com.flash21.caddycom.dto.golfFieldDetail.hole.HoleRequest;
import com.flash21.caddycom.service.golfFieldDetail.HoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Tag(name="2-3. Hole", description = "홀 정보 관련 API")
@RestController
@RequiredArgsConstructor
public class HoleController {
    private final HoleService holeService;

    @PatchMapping("/api/hole/handicap")
    @Operation(summary = "홀의 핸디 수 변경 API")
    public void updateHandicap(@Valid  @RequestBody HoleRequest.UpdateHandicap request) {
        holeService.updateHandicap(request);
    }

    @PatchMapping("/api/hole/par")
    @Operation(summary = "홀의 파 변경 API")
    public void updatePar(@Valid @RequestBody HoleRequest.UpdatePar request) {
        holeService.updatePar(request);
    }

    @PostMapping(value="/api/hole/detail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "홀의 상세 정보 설정 API")
    public void createDetailInfo(@Valid @ModelAttribute HoleRequest.CreateDetailInfo request) {
        holeService.createDetailInfo(request);
    }
}
