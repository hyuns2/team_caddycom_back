package com.flash21.caddycom.repository.golfField;

import com.flash21.caddycom.entity.golfField.GolfField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface GolfFieldRepository extends JpaRepository<GolfField, Long> {
    Optional<GolfField> findByRegistrationNumber(String registrationNumber);

    @Query("SELECT g FROM GolfField g WHERE g.id IN :ids")
    List<GolfField> findByIds(List<Long> ids);

    default GolfField getGolfFieldById(Long id) {
        return this.findById(id).orElseThrow(
                () -> new NoSuchElementException("해당 골프장은 존재하지 않습니다."));
    }
}
