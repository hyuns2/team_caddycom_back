package com.flash21.caddycom.repository.golfFieldDetail.course;

import com.flash21.caddycom.entity.golfFieldDetail.Course;

import java.util.List;

public interface CourseJdbcRepository {
    List<Long> saveAllInBatch(List<Course> courses);
}
