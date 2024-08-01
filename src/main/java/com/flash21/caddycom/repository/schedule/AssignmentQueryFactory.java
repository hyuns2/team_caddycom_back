package com.flash21.caddycom.repository.schedule;

import com.flash21.caddycom.entity.schedule.Assignment;
import com.flash21.caddycom.entity.schedule.AssignmentStatus;
import com.flash21.caddycom.entity.schedule.QAssignment;
import com.querydsl.core.types.dsl.BooleanExpression;
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


    // TODO: 코스 이름은 schedule.course 에서 가져올 수 있도록 조인 작업 추가로 필요
    public Page<Assignment> findAllByDateAndCourseIdAndStatus(Pageable pageable, Long golfFieldId, LocalDate date, Long courseId, AssignmentStatus status){
        List<Assignment> assignments = jpaQueryFactory
                .selectFrom(QAssignment.assignment)
                .where(QAssignment.assignment.schedule.golfField.id.eq(golfFieldId)
                        .and(QAssignment.assignment.schedule.reservationAt.eq(date))
                        .and(eqCourseId(courseId))
                        .and(eqStatus(status)))
                .orderBy(QAssignment.assignment.startTime.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        Long total = jpaQueryFactory
                .select(QAssignment.assignment.count())
                .from(QAssignment.assignment)
                .where(QAssignment.assignment.schedule.golfField.id.eq(golfFieldId)
                        .and(QAssignment.assignment.schedule.reservationAt.eq(date))
                        .and(eqCourseId(courseId))
                        .and(eqStatus(status)))
                .fetchFirst();
        return new PageImpl<>(assignments, pageable, total == null ? 0 : total);
    }


    public Page<Assignment> findAssignedByDateAndCourseIdAndPart(Pageable pageable, Long id, Long golfFieldId, LocalDate date, Long courseId, Integer part){
        List<Assignment> assignments = jpaQueryFactory
                .selectFrom(QAssignment.assignment)
                .where(QAssignment.assignment.schedule.golfField.id.eq(golfFieldId)
                        .and(QAssignment.assignment.id.ne(id))
                        .and(QAssignment.assignment.schedule.reservationAt.eq(date))
                        .and(eqCourseId(courseId))
                        .and(eqPart(part))
                        .and(eqStatus(AssignmentStatus.ASSIGNED)))
                .orderBy(QAssignment.assignment.startTime.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        Long total = jpaQueryFactory
                .select(QAssignment.assignment.count())
                .from(QAssignment.assignment)
                .where(QAssignment.assignment.schedule.golfField.id.eq(golfFieldId)
                        .and(QAssignment.assignment.id.ne(id))
                        .and(QAssignment.assignment.schedule.reservationAt.eq(date))
                        .and(eqCourseId(courseId))
                        .and(eqPart(part))
                        .and(eqStatus(AssignmentStatus.ASSIGNED)))
                .fetchFirst();
        return new PageImpl<>(assignments, pageable, total == null ? 0 : total);
    }


    private BooleanExpression eqPart(Integer part) {
        return part == null ? null : QAssignment.assignment.schedule.part.eq(part);
    }


    private BooleanExpression eqCourseId(Long courseId) {
        return courseId == null ? null : QAssignment.assignment.schedule.course.id.eq(courseId);
    }


    private BooleanExpression eqStatus(AssignmentStatus status) {
        return status == null ? null : QAssignment.assignment.status.eq(status);
    }
}
