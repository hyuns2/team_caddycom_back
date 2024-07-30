package com.flash21.caddycom.dto.caddy;

import com.flash21.caddycom.entity.caddy.Days;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class HouseCaddyDto {
    @Getter
    @AllArgsConstructor
    public static class HolidayInfo{
        private Long id;
        private String name;
        private String teamRole;
        private List<Days> holiday;
    }
}
