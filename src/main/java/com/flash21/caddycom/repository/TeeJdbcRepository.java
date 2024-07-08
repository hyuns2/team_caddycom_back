package com.flash21.caddycom.repository;

import com.flash21.caddycom.entity.Tee;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TeeJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<Tee> tees) {
        String sql = "INSERT INTO TEE (name, distance, hole_id)" + "VALUES (?, ?, ?)";
        
        jdbcTemplate.batchUpdate(sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Tee tee = tees.get(i);
                        ps.setString(1, tee.getName());
                        ps.setInt(2, tee.getDistance());
                        ps.setLong(3, tee.getHole().getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return tees.size();
                    }
                });
    }
}
