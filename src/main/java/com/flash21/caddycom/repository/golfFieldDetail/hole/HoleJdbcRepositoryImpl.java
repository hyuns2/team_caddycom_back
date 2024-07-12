package com.flash21.caddycom.repository.golfFieldDetail.hole;

import com.flash21.caddycom.entity.golfFieldDetail.Hole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class HoleJdbcRepositoryImpl implements HoleJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public List<Long> saveAllInBatch(List<Hole> holes) {
        String sql = "INSERT INTO HOLE (num, par, handicap, course_id)" + "VALUES (?, ?, ?, ?)";
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
                Hole hole = holes.get(i);
                ps.setInt(1, hole.getNum());
                ps.setInt(2, hole.getPar());
                ps.setInt(3, hole.getHandicap());
                ps.setLong(4, hole.getCourse().getId());
            }

            @Override
            public int getBatchSize() {
                return holes.size();
            }
        }, keyHolder);

        List<Map<String, Object>> keyList = keyHolder.getKeyList();
        List<Long> generatedIds = new ArrayList<>();
        for(Map<String, Object> key : keyList) {
            generatedIds.add((Long) key.get("id"));
        }

        return generatedIds;
    }
}
