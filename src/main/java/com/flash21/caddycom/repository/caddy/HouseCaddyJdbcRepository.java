package com.flash21.caddycom.repository.caddy;

import com.flash21.caddycom.entity.caddy.HouseCaddy;

import java.util.List;

public interface HouseCaddyJdbcRepository {
    void bulkInsert(List<HouseCaddy> houseCaddyList, Long golfFieldId);
}
