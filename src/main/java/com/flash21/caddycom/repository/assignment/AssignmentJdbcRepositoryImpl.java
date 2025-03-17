package com.flash21.caddycom.repository.assignment;

import com.flash21.caddycom.entity.schedule.Assignment;
import com.flash21.caddycom.entity.schedule.AssignmentStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AssignmentJdbcRepositoryImpl implements AssignmentJdbcRepository{
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void bulkInsert(List<Assignment> assignments) {
        String sql = "INSERT INTO assignment"
                + " (schedule_id, start_time, assignment_status, caddy_id, caddy_name, reason)"
                + " VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, assignments, assignments.size(),
                (ps, assignment) -> {
                    ps.setLong(1, assignment.getSchedule().getId());
                    ps.setTime(2, Time.valueOf(assignment.getStartTime()));
                    ps.setInt(3, AssignmentStatus.NOTHING.getNumber());
                    ps.setNull(4, Types.LONGVARBINARY);
                    ps.setString(5, null);
                    ps.setString(6, null);
                });
    }
}
