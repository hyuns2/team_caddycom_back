package com.flash21.caddycom.repository.golfFieldDetail.hole;

import com.flash21.caddycom.entity.golfFieldDetail.Hole;

import java.util.List;

public interface HoleJdbcRepository {
    List<Long> saveAllInBatch(List<Hole> holes);
}
