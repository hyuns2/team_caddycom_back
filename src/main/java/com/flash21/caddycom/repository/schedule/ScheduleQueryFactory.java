package com.flash21.caddycom.repository.schedule;

import com.flash21.caddycom.entity.schedule.Schedule;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleQueryFactory {

    List<Schedule> findAllByDateFetchJoinToAssignmentAndHouseCaddy(Long golfFieldId, LocalDate date);

}
