package com.flash21.caddycom.repository.golfFieldDetail.tee;

import com.flash21.caddycom.entity.golfFieldDetail.Hole;
import com.flash21.caddycom.entity.golfFieldDetail.Tee;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TeeRepository extends JpaRepository<Tee, Long>, TeeJdbcRepository {
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "delete from Tee t where t.hole.course.id = :courseId ")
    void deleteAllByCourseId(Long courseId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "delete from Tee t where t.hole in :holes")
    void deleteAllByHoles(Iterable<Hole> holes);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "delete from Tee t where t.hole.course.formation.id in :formationIds")
    void deleteAllByFormationIds(Iterable<Long> formationIds);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "delete from Tee t where t.hole.course.id in :courseIds")
    void deleteAllByCourseIds(Iterable<Long> courseIds);
}
