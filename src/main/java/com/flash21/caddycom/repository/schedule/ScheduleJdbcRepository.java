package com.flash21.caddycom.repository.schedule;

import com.flash21.caddycom.entity.schedule.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScheduleJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<Schedule> scheduleList) {
        String sql = "INSERT INTO schedule"
                + " (golf_field_id, course_id, reservation_at, start_time, end_time, tee_off, part, date_status, total_cnt, blocked_cnt, reservation_sheet_id)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
                return ps;
            }
        }, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Schedule target = scheduleList.get(i);
                ps.setLong(1, target.getGolfField().getId());
                ps.setLong(2, target.getCourse().getId());
                ps.setDate(3, Date.valueOf(target.getReservationAt()));
                ps.setTime(4, Time.valueOf(target.getStartTime()));
                ps.setTime(5, Time.valueOf(target.getEndTime()));
                ps.setString(6, target.getTeeOff());
                ps.setInt(7, target.getPart());
                ps.setInt(8, target.getDateStatus().getNumber());
                ps.setInt(9, target.getTotalCnt());
                ps.setInt(10, target.getBlockedCnt());
                ps.setLong(11, target.getReservationSheet().getId());
            }

            @Override
            public int getBatchSize() {
                return scheduleList.size();
            }
        }, null);
    }
}
