package com.flash21.caddycom.repository;

import com.flash21.caddycom.entity.golfFieldDetail.Hole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HoleRepository extends JpaRepository<Hole, Long> {
    Optional<List<Hole>> findAllByCourseId(Long courseId);
}
