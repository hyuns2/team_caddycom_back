package com.flash21.caddycom.repository.golfField;

import com.flash21.caddycom.entity.golfField.Facility;
import com.flash21.caddycom.entity.golfField.GolfField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacilityRepository extends JpaRepository<Facility, Long> {
    List<Facility> findAllByGolfField(GolfField golfField);
}
