package com.flash21.caddycom.repository.golfField;

import com.flash21.caddycom.entity.golfField.FacilityImage;

import java.util.List;

public interface FacilityImageJdbcRepository {
    void bulkInsert(List<FacilityImage> facilityImages);
}
