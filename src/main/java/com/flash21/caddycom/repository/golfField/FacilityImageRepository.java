package com.flash21.caddycom.repository.golfField;

import com.flash21.caddycom.entity.golfField.FacilityImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityImageRepository extends JpaRepository<FacilityImage, Long>, FacilityImageJdbcRepository {
}
