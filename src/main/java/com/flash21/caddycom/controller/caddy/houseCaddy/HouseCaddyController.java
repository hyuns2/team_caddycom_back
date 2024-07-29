package com.flash21.caddycom.controller.caddy.houseCaddy;

import com.flash21.caddycom.dto.caddy.HouseCaddyDto;
import com.flash21.caddycom.service.caddy.houseCaddy.HouseCaddyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "4. House Caddy", description = "하우스캐디 API")
@RequestMapping("/api/house-caddy")
public class HouseCaddyController {
    final HouseCaddyService houseCaddyService;

    @Operation(summary = "조 조회", description = "골프장에 속해있는 하우스 캐디의 모든 조를 조회합니다.")
    @GetMapping("/team/{golfFieldId}")
    public ResponseEntity<?> getHouseCaddyTeam(@AuthenticationPrincipal User user, @PathVariable Long golfFieldId) {
        List<String> result = houseCaddyService.getHouseCaddyTeam(golfFieldId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "조별 조회", description = "해당하는 조에 속해있는 하우스 캐디들을 조회합니다.")
    @GetMapping("/{teamName}/{page}")
    public ResponseEntity<?> getHouseCaddyByTeam(@AuthenticationPrincipal User user, @PathVariable String teamName, @PathVariable int page) {
        List<HouseCaddyDto.houseCaddyResponse> result = houseCaddyService.getHouseCaddyByTeam(teamName, page);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
