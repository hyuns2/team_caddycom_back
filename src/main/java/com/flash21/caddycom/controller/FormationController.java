package com.flash21.caddycom.controller;

import com.flash21.caddycom.dto.formation.FormationAdd;
import com.flash21.caddycom.service.FormationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "2-1. Formation", description = "골프장 구성 설정 관련 API")
@RestController
@RequiredArgsConstructor
public class FormationController {
    private final FormationService formationService;

    @PostMapping("/api/formations")
    @Operation(summary = "골프장 구성 정보 생성 API")
    public ResponseEntity<Void> addFormation(@RequestBody FormationAdd request) {
        formationService.addFormation(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
