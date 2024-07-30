package com.flash21.caddycom.controller.caddy;

import com.flash21.caddycom.dto.caddy.HouseCaddyResponse;
import com.flash21.caddycom.service.caddy.HouseCaddyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/house-caddy")
public class HouseCaddyController {
    private final HouseCaddyService houseCaddyService;

    @GetMapping("/holiday/{golfFieldId}")
    public ResponseEntity<List<HouseCaddyResponse.TeamHoliday>> getAllHolidayInfo(@PathVariable("golfFieldId") Long golfFieldId) {
        List<HouseCaddyResponse.TeamHoliday> allHoliday = houseCaddyService.getAllHoliday(golfFieldId);

        return ResponseEntity.ok(allHoliday);
    }

    @GetMapping("/holiday/{golfFieldId}/team")
    public ResponseEntity<HouseCaddyResponse.TeamHoliday> getTeamHolidayInfo(@PathVariable("golfFieldId") Long golfFieldId, String name) {
        HouseCaddyResponse.TeamHoliday holiday = houseCaddyService.getTeamHoliday(golfFieldId, name);

        return ResponseEntity.ok(holiday);
    }
}
