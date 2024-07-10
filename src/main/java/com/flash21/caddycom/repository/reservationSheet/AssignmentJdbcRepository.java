package com.flash21.caddycom.repository.reservationSheet;

import com.flash21.caddycom.dto.reservationSheet.AssignmentDto;
import com.flash21.caddycom.entity.reservationSheet.Assignment;
import com.flash21.caddycom.entity.reservationSheet.AssignmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class AssignmentJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public List<AssignmentDto.AssignmentsResponse> saveAll(List<Assignment> assignmentList) {
        String sql = "INSERT INTO ASSIGNMENT"
                + "(reservation_date, start_time, status, caddyName, reason)"
                + "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.batchUpdate(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
                return ps;
            }
        }, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Assignment assignment = assignmentList.get(i);
                ps.setLong(1, assignment.getReservationDate().getId());
                ps.setTime(2, Time.valueOf(assignment.getStartTime()));
                ps.setString(3, assignment.getStatus().toString());
                ps.setString(4, assignment.getCaddyName());
                ps.setString(5, assignment.getReason());
            }

            @Override
            public int getBatchSize() {
                return assignmentList.size();
            }
        }, keyHolder);

        List<Map<String, Object>> keyList = keyHolder.getKeyList();
        List<AssignmentDto.AssignmentsResponse> dtoList = new ArrayList<>();
        for(Map<String, Object> key : keyList) {
            dtoList.add(AssignmentDto.AssignmentsResponse.builder().
                    id((Long)key.get("id")).
                    reservationDate((LocalDate) key.get("reservation_date")).
                    startTime((LocalTime) key.get("start_time")).
                    status((AssignmentStatus) key.get("status")).
                    caddyName("caddy_name").
                    reason("reason").build());
        }

        return dtoList;
    }
}
