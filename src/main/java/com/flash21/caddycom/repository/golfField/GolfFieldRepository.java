package com.flash21.caddycom.repository.golfField;

import com.flash21.caddycom.entity.golfField.GolfField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GolfFieldRepository extends JpaRepository<GolfField, Long> {
    Optional<GolfField> findByRegistrationNumber(String registrationNumber);
}
