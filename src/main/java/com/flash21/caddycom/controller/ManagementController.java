package com.flash21.caddycom.controller;

import com.flash21.caddycom.dto.formation.FormationAdd;
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
    @PostMapping("/permission")
    @Operation(summary = "골프장 등록 승인 API")
    public ResponseEntity<Void> approve(@RequestParam Long golfFieldId) {
        managementService.approveRegistration(golfFieldId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
