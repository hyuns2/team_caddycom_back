package com.flash21.caddycom.controller.caddy;


import com.flash21.caddycom.dto.caddy.HouseCaddyRequest;
import com.flash21.caddycom.dto.caddy.HouseCaddyResponse;
import com.flash21.caddycom.service.caddy.HouseCaddyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "3-1. House Caddy Management", description = "하우스 캐디 관리 API")
@RequestMapping("/api/house-caddy")
public class CaddyManagementController {
    //TODO: 캐디 엑셀 업로드 추가 필요

    private final HouseCaddyService houseCaddyService;

    @Operation(summary = "조 전체조회", description = "골프장에 속해있는 하우스 캐디의 모든 조를 조회합니다.")
    @GetMapping("/team/{golfFieldId}")
    public ResponseEntity<List<String>> getHouseCaddyTeam(@PathVariable Long golfFieldId) {
        List<String> result = houseCaddyService.getHouseCaddyTeam(golfFieldId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "하우스캐디 정보 일괄조회", description = "해당하는 조에 속해있는 하우스 캐디들의 정보를 조회합니다.")
    @GetMapping("/{golfFieldId}/all")
    public ResponseEntity<Map<String, List<HouseCaddyResponse.Detail>>> getHouseCaddies(@PathVariable Long golfFieldId) {
        Map<String, List<HouseCaddyResponse.Detail>> result = houseCaddyService.getHouseCaddies(golfFieldId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "하우스캐디 정보 조별조회", description = "해당하는 조에 속해있는 하우스 캐디들의 정보를 조회합니다.")
    @GetMapping("/{golfFieldId}/{teamName}")
    public ResponseEntity<List<HouseCaddyResponse.Detail>> getHouseCaddyByTeam(@PathVariable Long golfFieldId, @PathVariable String teamName) {
        List<HouseCaddyResponse.Detail> result = houseCaddyService.getHouseCaddyByTeam(golfFieldId, teamName);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "관리자의 하우스캐디 정보변경", description = "관리자가 하우스캐디의 정보를 변경합니다.")
    @PutMapping("/{golfFieldId}/{caddyId}")
    public ResponseEntity<Void> updateHouseCaddyByManager(@PathVariable Long golfFieldId,
                                                          @PathVariable Long caddyId,
                                                          @Valid @RequestBody HouseCaddyRequest.UpdateByManager dto) {
        houseCaddyService.updateHouseCaddyByManager(golfFieldId, caddyId, dto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "캐디 목록 전체 조회", description = "골프장에 속해있는 모든 하우스 캐디들을 모두 조회합니다.")
    @GetMapping("/all/{golfFieldId}")
    public ResponseEntity<Map<String, List<HouseCaddyResponse.Info>>> getAllHouseCaddies(
            @PathVariable("golfFieldId") Long golfFieldId,
            @ModelAttribute HouseCaddyRequest.CaddySearchCond searchCond
    ) {
        Map<String, List<HouseCaddyResponse.Info>> result =
                houseCaddyService.getAllHouseCaddy(golfFieldId, searchCond);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "캐디 휴무일 전체 조회", description = "골프장에 속해있는 모든 하우스 캐디의 휴무일을 조회합니다.")
    @GetMapping("/holiday/{golfFieldId}")
    public ResponseEntity<List<HouseCaddyResponse.TeamHoliday>> getAllHolidayInfo(@PathVariable("golfFieldId") Long golfFieldId) {
        List<HouseCaddyResponse.TeamHoliday> allHoliday = houseCaddyService.getAllHoliday(golfFieldId);

        return ResponseEntity.ok(allHoliday);
    }

    @Operation(summary = "캐디 휴무일 조별 조회", description = "특정 조의 전체 인원의 휴무일을 조회합니다.")
    @GetMapping("/holiday/{golfFieldId}/team")
    public ResponseEntity<HouseCaddyResponse.TeamHoliday> getTeamHolidayInfo(@PathVariable("golfFieldId") Long golfFieldId, String name) {
        HouseCaddyResponse.TeamHoliday holiday = houseCaddyService.getTeamHoliday(golfFieldId, name);

        return ResponseEntity.ok(holiday);
    }

    @Operation(summary = "캐디 휴무일 일괄 변경", description = "하우스 캐디의 휴무일을 일괄적으로 변경합니다.")
    @PostMapping("/holiday")
    public ResponseEntity<Void> updateHolidayAll(@Valid @RequestBody List<HouseCaddyRequest.CreateHoliday> request) {
        houseCaddyService.updateHolidayAll(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "하우스캐디 휴무일 승인", description = "관리자가 하우스캐디의 휴무일을 승인합니다.")
    @PostMapping("/holiday/{caddyId}")
    public ResponseEntity<Void> updateHouseCaddyHoliday(@PathVariable Long caddyId) {
        houseCaddyService.updateHouseCaddyHoliday(caddyId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
