package com.flash21.caddycom.repository.schedule;

import com.flash21.caddycom.entity.schedule.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScheduleJdbcRepositoryImpl implements ScheduleJdbcRepository{
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void bulkInsert(List<Schedule> schedules) {
        String sql = "INSERT INTO schedule"
                + " (golf_field_id, course_id, reservation_at, start_time, end_time, tee_off, part, date_status, total_cnt, blocked_cnt, not_assigned_cnt, reservation_sheet_id)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, schedules, schedules.size(),
                (ps, schedule) -> {
                    ps.setLong(1, schedule.getGolfField().getId());
                    ps.setLong(2, schedule.getCourse().getId());
                    ps.setDate(3, Date.valueOf(schedule.getReservationAt()));
                    ps.setTime(4, Time.valueOf(schedule.getStartTime()));
                    ps.setTime(5, Time.valueOf(schedule.getEndTime()));
                    ps.setString(6, schedule.getTeeOff());
                    ps.setInt(7, schedule.getPart());
                    ps.setInt(8, schedule.getDateStatus().getNumber());
                    ps.setInt(9, schedule.getTotalCnt());
                    ps.setInt(10, schedule.getBlockedCnt());
                    ps.setInt(11, schedule.getNotAssignedCnt());
                    ps.setLong(12, schedule.getReservationSheet().getId());
                });
    }
}
