package com.flash21.caddycom.controller.golfFieldDetail;

import com.flash21.caddycom.dto.golfFieldDetail.hole.HoleRequest;
import com.flash21.caddycom.dto.golfFieldDetail.hole.HoleResponse;
import com.flash21.caddycom.service.golfFieldDetail.HoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/api/{courseId}/holes")
    @Operation(summary = "코스의 모든 홀 정보 조회 API")
    public ResponseEntity<List<HoleResponse.Info>> getHoles(@PathVariable Long courseId) {
        List<HoleResponse.Info> holeInfos = holeService.getHoles(courseId);

        return new ResponseEntity<>(holeInfos, HttpStatus.OK);
    }
}
