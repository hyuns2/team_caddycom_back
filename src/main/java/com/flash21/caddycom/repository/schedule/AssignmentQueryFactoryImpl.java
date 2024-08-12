package com.flash21.caddycom.repository.schedule;

import com.flash21.caddycom.entity.schedule.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.flash21.caddycom.entity.golfField.QGolfField.*;
import static com.flash21.caddycom.entity.golfFieldDetail.QCourse.*;
import static com.flash21.caddycom.entity.schedule.QAssignment.*;
import static com.flash21.caddycom.entity.schedule.QSchedule.*;

@Repository
@RequiredArgsConstructor
public class AssignmentQueryFactoryImpl implements AssignmentQueryFactory {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Assignment> findByIdsFetchJoinOrderByStartTime(List<Long> ids) {
        return jpaQueryFactory
                .selectFrom(assignment)
                .join(assignment.schedule, schedule).fetchJoin()
                .join(schedule.golfField, golfField).fetchJoin()
                .join(golfField.formations).fetchJoin()
                .join(schedule.course, course).fetchJoin()
                .where(assignment.id.in(ids))
                .orderBy(assignment.startTime.asc())
                .fetch();
    }

    @Override
    public Optional<Assignment> findByIdWithFetchJoin(Long assignmentId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(assignment)
                        .join(assignment.caddy).fetchJoin()
                        .join(assignment.schedule, schedule).fetchJoin()
                        .join(schedule.golfField, golfField).fetchJoin()
                        .join(schedule.course, course).fetchJoin()
                        .join(course.formation).fetchJoin()
                        .where(assignment.id.eq(assignmentId))
                        .fetchFirst());
    }

    public Page<Assignment> findAllByDateAndCourseIdAndStatus(Pageable pageable, Long golfFieldId, LocalDate date, Long courseId, AssignmentStatus status) {
        List<Assignment> assignments = jpaQueryFactory
                .selectFrom(assignment)
                .join(assignment.schedule, schedule).fetchJoin()
                .join(schedule.course, course).fetchJoin()
                .where(assignment.schedule.golfField.id.eq(golfFieldId)
                        .and(assignment.schedule.reservationAt.eq(date))
                        .and(eqCourseId(courseId))
                        .and(eqStatus(status)))
                .orderBy(assignment.startTime.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        Long total = jpaQueryFactory
                .select(assignment.count())
                .from(assignment)
                .where(assignment.schedule.golfField.id.eq(golfFieldId)
                        .and(assignment.schedule.reservationAt.eq(date))
                        .and(eqCourseId(courseId))
                        .and(eqStatus(status)))
                .fetchFirst();
        return new PageImpl<>(assignments, pageable, total == null ? 0 : total);
    }


    public Page<Assignment> findAssignedByDateAndCourseIdAndPart(Pageable pageable, Long id, Long golfFieldId, LocalDate date, Long courseId, Integer part) {
        List<Assignment> assignments = jpaQueryFactory
                .selectFrom(assignment)
                .join(assignment.schedule, schedule).fetchJoin()
                .join(schedule.golfField).fetchJoin()
                .join(schedule.course, course).fetchJoin()
                .join(course.formation).fetchJoin()
                .where(assignment.schedule.golfField.id.eq(golfFieldId)
                        .and(assignment.id.ne(id))
                        .and(assignment.schedule.reservationAt.eq(date))
                        .and(eqCourseId(courseId))
                        .and(eqPart(part))
                        .and(eqStatus(AssignmentStatus.ASSIGNED)))
                .orderBy(assignment.startTime.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        Long total = jpaQueryFactory
                .select(assignment.count())
                .from(assignment)
                .where(assignment.schedule.golfField.id.eq(golfFieldId)
                        .and(assignment.id.ne(id))
                        .and(assignment.schedule.reservationAt.eq(date))
                        .and(eqCourseId(courseId))
                        .and(eqPart(part))
                        .and(eqStatus(AssignmentStatus.ASSIGNED)))
                .fetchFirst();
        return new PageImpl<>(assignments, pageable, total == null ? 0 : total);
    }

    @Override
    public List<Assignment> findByCaddyIdAndMonth(Long caddyId, LocalDate startDate, LocalDate endDate) {
        return jpaQueryFactory
                .selectFrom(assignment)
                .where(assignment.caddy.id.eq(caddyId)
                        .and(assignment.schedule.reservationAt.between(startDate, endDate)))
                .leftJoin(assignment.schedule, schedule).fetchJoin()
                .leftJoin(schedule.golfField).fetchJoin()
                .orderBy(assignment.schedule.reservationAt.asc())
                .fetch();
    }

    private BooleanExpression eqPart(Integer part) {
        return part == 0 ? null : QAssignment.assignment.schedule.part.eq(part);

    }


    private BooleanExpression eqCourseId(Long courseId) {
        return courseId == 0 ? null : QAssignment.assignment.schedule.course.id.eq(courseId);

    }


    private BooleanExpression eqStatus(AssignmentStatus status) {
        return status == null ? null : assignment.status.eq(status);
    }
}
