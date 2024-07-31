package com.flash21.caddycom.repository.caddy;

import com.flash21.caddycom.dto.caddy.HouseCaddyRequestDto;
import com.flash21.caddycom.entity.caddy.HouseCaddy;

import java.util.List;

public interface HouseCaddyQueryRepository {

    List<HouseCaddy> findAllByGolfFieldIdAndSearchCond(Long goldFieldId, HouseCaddyRequestDto.CaddySearchCond searchCond);
}
