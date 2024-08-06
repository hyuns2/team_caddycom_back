package com.flash21.caddycom.controller.caddy;

import com.flash21.caddycom.dto.caddy.HouseCaddyRequestDto;
import com.flash21.caddycom.dto.caddy.HouseCaddyResponseDto;
import com.flash21.caddycom.service.caddy.HouseCaddyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "4. House Caddy", description = "하우스캐디 API")
@RequestMapping("/api/house-caddy")
public class HouseCaddyController {
    final HouseCaddyService houseCaddyService;

    @Operation(summary = "조 전체조회", description = "골프장에 속해있는 하우스 캐디의 모든 조를 조회합니다.")
    @GetMapping("/team/{golfFieldId}")
    public ResponseEntity<List<String>> getHouseCaddyTeam(@PathVariable Long golfFieldId) {
        List<String> result = houseCaddyService.getHouseCaddyTeam(golfFieldId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "조별 조회", description = "해당하는 조에 속해있는 하우스 캐디들을 조회합니다.")
    @GetMapping("/{golfFieldId}/{teamName}")
    public ResponseEntity<List<HouseCaddyResponseDto.houseCaddyDetail>> getHouseCaddyByTeam(@PathVariable Long golfFieldId, @PathVariable String teamName) {
        List<HouseCaddyResponseDto.houseCaddyDetail> result = houseCaddyService.getHouseCaddyByTeam(golfFieldId, teamName);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "하우스캐디 정보변경", description = "하우스캐디의 정보를 변경합니다.")
    @PutMapping("/{golfFieldId}/{caddyId}")
    public ResponseEntity<Void> updateHouseCaddy(@PathVariable Long golfFieldId, @PathVariable Long caddyId, @Valid @RequestBody HouseCaddyRequestDto.updateHouseCaddy dto) {
        houseCaddyService.updateHouseCaddy(golfFieldId, caddyId, dto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "하우스캐디 휴무일 승인", description = "하우스캐디의 휴무일을 승인합니다.")
    @PostMapping("/holiday/{caddyId}")
    public ResponseEntity<Void> updateHouseCaddyHoliday(@PathVariable Long caddyId) {
        houseCaddyService.updateHouseCaddyHoliday(caddyId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "캐디 목록 전체 조회", description = "골프장에 속해있는 모든 하우스 캐디들을 모두 조회합니다.")
    @GetMapping("/all/{golfFieldId}")
    public ResponseEntity<Map<String, List<HouseCaddyResponseDto.Info>>> getAllHouseCaddies(
            @AuthenticationPrincipal User user,
            @PathVariable("golfFieldId") Long golfFieldId,
            @ModelAttribute HouseCaddyRequestDto.CaddySearchCond searchCond
    ) {
        Map<String, List<HouseCaddyResponseDto.Info>> result =
                houseCaddyService.getAllHouseCaddy(golfFieldId, searchCond);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "캐디 휴무일 전체 조회", description = "골프장에 속해있는 모든 하우스 캐디의 휴무일을 조회합니다.")
    @GetMapping("/holiday/{golfFieldId}")
    public ResponseEntity<List<HouseCaddyResponseDto.TeamHoliday>> getAllHolidayInfo(@PathVariable("golfFieldId") Long golfFieldId) {
        List<HouseCaddyResponseDto.TeamHoliday> allHoliday = houseCaddyService.getAllHoliday(golfFieldId);

        return ResponseEntity.ok(allHoliday);
    }

    @Operation(summary = "캐디 휴무일 조별 조회", description = "특정 조의 전체 인원의 휴무일을 조회합니다.")
    @GetMapping("/holiday/{golfFieldId}/team")
    public ResponseEntity<HouseCaddyResponseDto.TeamHoliday> getTeamHolidayInfo(@PathVariable("golfFieldId") Long golfFieldId, String name) {
        HouseCaddyResponseDto.TeamHoliday holiday = houseCaddyService.getTeamHoliday(golfFieldId, name);

        return ResponseEntity.ok(holiday);
    }

    @Operation(summary = "캐디 휴무일 일괄 변경", description = "하우스 캐디의 휴무일을 일괄적으로 변경합니다.")
    @PostMapping("/holiday")
    public ResponseEntity<Void> updateHolidayAll(@Valid @RequestBody List<HouseCaddyRequestDto.createHoliday> request) {
        houseCaddyService.updateHolidayAll(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
