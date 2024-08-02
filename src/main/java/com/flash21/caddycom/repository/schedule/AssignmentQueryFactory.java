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

import static com.flash21.caddycom.entity.caddy.QHouseCaddy.houseCaddy;
import static com.flash21.caddycom.entity.schedule.QAssignment.*;
import static com.flash21.caddycom.entity.schedule.QSchedule.*;

@Repository
@RequiredArgsConstructor
public class AssignmentQueryFactory {
    private final JPAQueryFactory jpaQueryFactory;


    // TODO: 코스 이름은 schedule.course 에서 가져올 수 있도록 조인 작업 추가로 필요
    public Page<Assignment> findAllByDateAndCourseIdAndStatus(Pageable pageable, Long golfFieldId, LocalDate date, Long courseId, AssignmentStatus status) {
        List<Assignment> assignments = jpaQueryFactory
                .selectFrom(assignment)
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


    public List<Schedule> findAllByGolfFieldIdAndReservationAtFetchJoinAssignmentAndHouseCaddy(Long golfFieldId, LocalDate date) {
        return jpaQueryFactory.selectFrom(schedule)
                .leftJoin(schedule.assignments, assignment).fetchJoin()
                .leftJoin(assignment.houseCaddy, houseCaddy).fetchJoin()
                .where(schedule.golfField.id.eq(golfFieldId)
                        .and(schedule.reservationAt.eq(date)))
                .orderBy(schedule.course.id.asc())
                .fetch();
    }

    private BooleanExpression eqPart(Integer part) {
        return part == null ? null : assignment.schedule.part.eq(part);
    }


    private BooleanExpression eqCourseId(Long courseId) {
        return courseId == null ? null : assignment.schedule.course.id.eq(courseId);
    }


    private BooleanExpression eqStatus(AssignmentStatus status) {
        return status == null ? null : assignment.status.eq(status);
    }
}
