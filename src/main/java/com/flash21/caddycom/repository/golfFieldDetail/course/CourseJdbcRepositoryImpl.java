package com.flash21.caddycom.repository.golfFieldDetail.course;

import com.flash21.caddycom.entity.golfFieldDetail.Course;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.*;
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
public class CourseJdbcRepositoryImpl implements CourseJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public List<Long> saveAllInBatch(List<Course> courses) {
        String sql = "INSERT INTO COURSE (name, total_holes, formation_id)" + "VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

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
                Course course = courses.get(i);
                ps.setString(1, course.getName());
                ps.setInt(2, course.getTotalHoles());
                ps.setLong(3, course.getFormation().getId());
            }

            @Override
            public int getBatchSize() {
                return courses.size();
            }
        }, keyHolder);

List<Map<String,Object>> keyList = keyHolder.getKeyList();
        List<Long> generatedIds = new ArrayList<>();
        for(Map<String, Object> key : keyList) {
            generatedIds.add((Long)key.get("id"));
        }
        return generatedIds;
    }
}
