package com.flash21.caddycom.repository.golfField;

import com.flash21.caddycom.entity.golfField.GolfField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

public interface GolfFieldRepository extends JpaRepository<GolfField, Long> {
    Optional<GolfField> findByRegistrationNumber(String registrationNumber);

    default GolfField getUserById(Long id) {
        return this.findById(id).orElseThrow(
                () -> new NoSuchElementException("해당 골프장은 존재하지 않습니다."));
    }
}
