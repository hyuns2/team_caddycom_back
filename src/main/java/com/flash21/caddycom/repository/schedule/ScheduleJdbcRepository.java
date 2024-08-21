package com.flash21.caddycom.repository.schedule;

import com.flash21.caddycom.entity.schedule.Schedule;

import java.util.List;

public interface ScheduleJdbcRepository {
    void saveAll(List<Schedule> scheduleList);
}
