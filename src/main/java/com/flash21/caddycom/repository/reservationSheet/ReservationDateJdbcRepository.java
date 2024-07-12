package com.flash21.caddycom.repository.reservationSheet;

import com.flash21.caddycom.entity.reservationSheet.ReservationDate;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReservationDateJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<ReservationDate> reservationDateList) {
        String sql = "INSERT INTO reservation_date"
                + "(reservation_sheet_id, reservation_at, is_assigned, total_cnt, blocked_cnt)"
                + "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
                return ps;
            }
        }, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ReservationDate target = reservationDateList.get(i);
                ps.setLong(1, target.getReservationSheet().getId());
                ps.setDate(2, Date.valueOf(target.getReservationAt()));
                ps.setBoolean(3, target.getIsAssigned());
                ps.setInt(4, target.getTotalCnt());
                ps.setInt(5, target.getBlockedCnt());
            }

            @Override
            public int getBatchSize() {
                return reservationDateList.size();
            }
        }, null);
    }
}
