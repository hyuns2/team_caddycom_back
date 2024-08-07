package com.flash21.caddycom.repository.golfFieldDetail.course;

import com.flash21.caddycom.entity.golfFieldDetail.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, CourseJdbcRepository {
    List<Course> findAllByFormationId(Long formationId);

    @Modifying
    @Transactional
    @Query(value = "delete from Course c where c.formation.id in :formationIds")
    void deleteAllByFormationIds(Iterable<Long> formationIds);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Course c SET c.deleted = true where c.id in :ids")
    void softDeleteAllByIdInBatch(Iterable<Long> ids);
}
