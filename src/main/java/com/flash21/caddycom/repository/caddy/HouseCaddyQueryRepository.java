package com.flash21.caddycom.repository.caddy;

import com.flash21.caddycom.dto.caddy.HouseCaddyResponse;
import com.flash21.caddycom.dto.caddy.CaddySearchCond;

import java.util.List;

public interface HouseCaddyQueryRepository {

    List<HouseCaddyResponse> findAllByGoldFieldIdAndSort(Long goldFieldId, CaddySearchCond searchCond);
}
