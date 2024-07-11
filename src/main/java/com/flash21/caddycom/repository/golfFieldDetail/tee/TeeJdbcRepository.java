package com.flash21.caddycom.repository.golfFieldDetail.tee;

import com.flash21.caddycom.entity.golfFieldDetail.Tee;

import java.util.List;

public interface TeeJdbcRepository {
    List<Long> saveAllInBatch(List<Tee> tees);
}
