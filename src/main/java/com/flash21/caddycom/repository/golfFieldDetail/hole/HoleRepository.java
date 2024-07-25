package com.flash21.caddycom.repository.golfFieldDetail.hole;

import com.flash21.caddycom.entity.golfFieldDetail.Hole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface HoleRepository extends JpaRepository<Hole, Long>, HoleJdbcRepository {
    Optional<List<Hole>> findAllByCourseId(Long courseId);

    @Query("select h from Hole h where h.course.id in :courseIds")
    Optional<List<Hole>> findAllByCourseIds(List<Long> courseIds);

    @Query("select distinct h from Hole h join fetch h.tees where h.course.id = :courseId")
    Optional<List<Hole>> findAllByCourseIdFetchJoinTee(Long courseId);

    @Modifying
    @Transactional
    @Query("delete from Hole h where h.course.formation.id in :formationIds")
    void deleteAllByFormationIds(Iterable<Long> formationIds);

    @Modifying
    @Transactional
    @Query("delete from Hole h where h.course.id in :courseIds")
    void deleteAllByCourseIds(Iterable<Long> courseIds);
}
