package com.flash21.caddycom.repository.golfFieldDetail.course;

import com.flash21.caddycom.entity.golfFieldDetail.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, CourseJdbcRepository {
    List<Course> findAllByFormationId(Long formationId);
}
