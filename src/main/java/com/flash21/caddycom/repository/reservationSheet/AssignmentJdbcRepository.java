package com.flash21.caddycom.repository.reservationSheet;

import com.flash21.caddycom.entity.reservationSheet.AssignmentStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AssignmentJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(Long reservationDateId, List<LocalTime> startTimeList) {
        String sql = "INSERT INTO assignment"
                + "(reservation_date_id, start_time, status, caddy_name, reason)"
                + "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(new PreparedStatementCreator() {
             @Override
             public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                 PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
                 return ps;
             }
         },
        new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                LocalTime targetTime = startTimeList.get(i);
                ps.setLong(1, reservationDateId);
                ps.setTime(2, Time.valueOf(targetTime));
                ps.setInt(3, AssignmentStatus.NOTHING.ordinal());
                ps.setString(4, null);
                ps.setString(5, null);
            }

            @Override
            public int getBatchSize() {
                return startTimeList.size();
            }
        }, null);
    }
}
