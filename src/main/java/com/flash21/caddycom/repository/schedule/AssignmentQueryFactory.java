package com.flash21.caddycom.repository.schedule;

import com.flash21.caddycom.entity.schedule.Assignment;
import com.flash21.caddycom.entity.schedule.QAssignment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AssignmentQueryFactory {
    private final JPAQueryFactory jpaQueryFactory;

    public Page<Assignment> findAllByDateAndCourseId(Pageable pageable, Long golfFieldId, LocalDate date, Long courseId){
        List<Assignment> assignments = jpaQueryFactory
                .selectFrom(QAssignment.assignment)
                .where(QAssignment.assignment.schedule.golfField.id.eq(golfFieldId)
                        .and(QAssignment.assignment.schedule.reservationAt.eq(date))
                        .and(QAssignment.assignment.schedule.course.id.eq(courseId)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(assignments, pageable, assignments.size());
    }
}
