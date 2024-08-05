package com.flash21.caddycom.repository.caddy;

import com.flash21.caddycom.dto.caddy.HouseCaddyRequestDto;
import com.flash21.caddycom.entity.caddy.HouseCaddy;

import java.util.List;


public interface HouseCaddyQueryFactory {

    List<HouseCaddy> findAllByGolfFieldIdAndSearchCond(Long golfFieldId, HouseCaddyRequestDto.CaddySearchCond searchCond);

}
