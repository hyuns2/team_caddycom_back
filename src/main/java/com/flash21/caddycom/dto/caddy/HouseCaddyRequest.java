package com.flash21.caddycom.dto.caddy;

import com.flash21.caddycom.entity.caddy.Days;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class HouseCaddyRequest {
    @Getter
    @AllArgsConstructor
    public static class createHoliday {
        private Long id;
        private List<Days> holidays;
    }
}
