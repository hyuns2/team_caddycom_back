package com.flash21.caddycom.controller.caddy;

import com.flash21.caddycom.dto.caddy.HouseCaddyRequestDto;
import com.flash21.caddycom.dto.caddy.HouseCaddyResponseDto;
import com.flash21.caddycom.service.caddy.HouseCaddyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Tag(name = "3-2. House Caddy", description = "하우스캐디 API")
@RequestMapping("/api/house-caddy")
public class HouseCaddyController {
    private final HouseCaddyService houseCaddyService;

    @Operation(summary = "하우스캐디의 정보 조회", description = "하우스캐디가 본인의 정보를 조회합니다.")
    @GetMapping("/{caddyId}")
    public ResponseEntity<HouseCaddyResponseDto.houseCaddyDetail> getHouseCaddy(@PathVariable Long caddyId) {
        HouseCaddyResponseDto.houseCaddyDetail result = houseCaddyService.getHouseCaddy(caddyId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "하우스캐디 정보변경 및 휴무일 요청", description = "하우스캐디가 본인의 정보를 변경합니다.")
    @PatchMapping(value = "/{caddyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateHouseCaddy(@PathVariable Long caddyId,
                                                 @ModelAttribute HouseCaddyRequestDto.updateHouseCaddy dto) {
        houseCaddyService.updateHouseCaddy(caddyId, dto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
