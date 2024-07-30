package com.flash21.caddycom.dto.caddy;

import com.flash21.caddycom.entity.caddy.Days;
import com.flash21.caddycom.entity.caddy.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class HouseCaddyResponse {
    @Getter
    @AllArgsConstructor
    public static class TeamHoliday {
        private String team;
        private List<HouseCaddyDto.HolidayInfo> info;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Info {
        private Long caddyId;
        private String team;
        private TeamRole teamRole;
        private String name;
        private List<Days> availDates;
    }
}
