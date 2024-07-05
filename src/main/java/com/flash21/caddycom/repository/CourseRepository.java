package com.flash21.caddycom.repository;

import com.flash21.caddycom.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
