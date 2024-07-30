package com.flash21.caddycom.dto.caddy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CaddyResponse {

    private String teamRole;
    private String name;
    private List<String> workAvailabilityDates;

}
