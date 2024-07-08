package com.flash21.caddycom.controller.management;

import com.flash21.caddycom.dto.golfField.GolfFieldResponse;
import com.flash21.caddycom.service.golfField.GolfFieldService;
import com.flash21.caddycom.service.management.ManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Management", description = "전체 시스템 관리 API")
@RequestMapping("/api/admin")
public class ManagementController {
    private final ManagementService managementService;
    private final GolfFieldService golfFieldService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/approve")
    @Operation(summary = "골프장 등록 승인 API", description="시스템 총 관리자만 접근 가능하다.")
    public ResponseEntity<Void> approve(@RequestParam Long golfFieldId) {
        managementService.approveRegistration(golfFieldId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/reject")
    @Operation(summary = "골프장 등록 거절 API", description="시스템 총 관리자만 접근 가능하다.")
    public ResponseEntity<Void> reject(@RequestParam Long golfFieldId) {
        managementService.rejectRegistration(golfFieldId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("golf-field")
    @Operation(summary = "모든 골프장 조회", description="시스템 총 관리자만 접근 가능하다. / UI에 맞춰 응답 수정 필요")
    public ResponseEntity<List<GolfFieldResponse.Overview>> getAll(){
        return ResponseEntity.ok().body(golfFieldService.getAll());
    }

}
