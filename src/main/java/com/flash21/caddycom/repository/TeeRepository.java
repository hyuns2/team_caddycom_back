package com.flash21.caddycom.repository;

import com.flash21.caddycom.entity.Tee;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TeeRepository extends JpaRepository<Tee, Long> {
    @Modifying
    @Transactional
    @Query(value = "delete from Tee t where t.hole.course.id = :courseId ")
    void deleteAllByCourseId(Long courseId);
}
