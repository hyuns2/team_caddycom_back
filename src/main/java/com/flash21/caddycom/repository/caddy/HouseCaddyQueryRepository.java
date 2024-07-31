package com.flash21.caddycom.repository.caddy;

import com.flash21.caddycom.dto.caddy.HouseCaddyRequestDto;
import com.flash21.caddycom.dto.caddy.HouseCaddyResponseDto;

import java.util.List;

public interface HouseCaddyQueryRepository {

    List<HouseCaddyResponseDto.Info> findAllByGoldFieldIdAndSort(Long goldFieldId, HouseCaddyRequestDto.CaddySearchCond searchCond);
}
