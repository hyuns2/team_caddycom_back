package com.flash21.caddycom.repository.schedule;

import com.flash21.caddycom.entity.caddy.Caddy;
import com.flash21.caddycom.entity.caddy.QCaddy;
import com.flash21.caddycom.entity.schedule.Schedule;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.flash21.caddycom.entity.caddy.QCaddy.*;
import static com.flash21.caddycom.entity.caddy.QHouseCaddy.houseCaddy;
import static com.flash21.caddycom.entity.schedule.QAssignment.assignment;
import static com.flash21.caddycom.entity.schedule.QSchedule.schedule;

@Repository
@RequiredArgsConstructor
public class ScheduleQueryFactory {

    private final JPAQueryFactory jpaQueryFactory;

    public List<Schedule> findAllByDateFetchJoinToAssignmentAndHouseCaddy(Long golfFieldId, LocalDate date) {
        return jpaQueryFactory.selectFrom(schedule)
                .leftJoin(schedule.assignments, assignment).fetchJoin()
                .leftJoin(assignment.caddy, caddy).fetchJoin()
                .where(schedule.golfField.id.eq(golfFieldId)
                        .and(schedule.reservationAt.eq(date)))
                .orderBy(schedule.course.id.asc())
                .fetch();
    }
}
