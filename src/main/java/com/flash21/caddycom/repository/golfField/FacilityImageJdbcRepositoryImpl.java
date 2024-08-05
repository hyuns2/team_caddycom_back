package com.flash21.caddycom.repository.golfField;

import com.flash21.caddycom.entity.golfField.FacilityImage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class FacilityImageJdbcRepositoryImpl implements FacilityImageJdbcRepository{
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void bulkInsert(List<FacilityImage> facilityImages) {
        String sql = "INSERT INTO facility_image (image_url, facility_id) " + "VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sql,
            new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    FacilityImage facilityImage = facilityImages.get(i);
                    ps.setString(1, facilityImage.getImageUrl());
                    ps.setLong(2, facilityImage.getFacility().getId());
                }

                @Override
                public int getBatchSize() {
                    return facilityImages.size();
                }
            });
    }
}
