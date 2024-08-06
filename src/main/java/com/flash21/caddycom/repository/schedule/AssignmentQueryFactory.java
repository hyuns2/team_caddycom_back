package com.flash21.caddycom.repository.schedule;

import com.flash21.caddycom.entity.schedule.Assignment;
import com.flash21.caddycom.entity.schedule.AssignmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AssignmentQueryFactory {
    Page<Assignment> findAllByDateAndCourseIdAndStatus(Pageable pageable, Long golfFieldId, LocalDate date, Long courseId, AssignmentStatus status);
    Page<Assignment> findAssignedByDateAndCourseIdAndPart(Pageable pageable, Long id, Long golfFieldId, LocalDate date, Long courseId, Integer part);
    List<Assignment> findAllByGolfFieldAndCaddyAndMonth(Long caddyId, LocalDate startDate, LocalDate endDate);

}
