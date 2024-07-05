package com.flash21.caddycom.controller;

import com.flash21.caddycom.service.management.ManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Management", description = "전체 시스템 관리 API")
@RequestMapping("/api/admin")
public class ManagementController {
    private final ManagementService managementService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/approve")
    @Operation(summary = "골프장 등록 승인 API")
    public ResponseEntity<Void> approve(@RequestParam Long golfFieldId) {
        managementService.approveRegistration(golfFieldId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/reject")
    @Operation(summary = "골프장 등록 거절 API")
    public ResponseEntity<Void> reject(@RequestParam Long golfFieldId) {
        managementService.rejectRegistration(golfFieldId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
