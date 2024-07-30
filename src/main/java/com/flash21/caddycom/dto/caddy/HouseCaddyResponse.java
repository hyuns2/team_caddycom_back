package com.flash21.caddycom.dto.caddy;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class HouseCaddyResponse {
    @Getter
    @AllArgsConstructor
    public static class TeamHoliday {
        private String team;
        private List<HouseCaddyDto.HolidayInfo> info;
    }
}
