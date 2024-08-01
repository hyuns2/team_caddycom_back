package com.flash21.caddycom.repository.caddy;

import com.flash21.caddycom.entity.caddy.Days;
import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.entity.caddy.converter.IntegerToStringConverter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class HouseCaddyJdbcRepository {
    private final JdbcTemplate jdbcTemplate;
    private final IntegerToStringConverter integerToStringConverter;

    @Transactional
    public void saveAll(List<HouseCaddy> houseCaddyList, Long golfFieldId) {
        String sql = "INSERT INTO house_caddy (golf_field_id, caddy_type, name, phone_number, gender, team, team_role, career, address, address_detail, off_part, holiday, birth) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql,
            new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    HouseCaddy houseCaddy = houseCaddyList.get(i);
                    ps.setLong(1, golfFieldId);
                    ps.setString(2, houseCaddy.getCaddyType());
                    ps.setString(3, houseCaddy.getName());
                    ps.setString(4, houseCaddy.getPhoneNumber());
                    ps.setInt(5, houseCaddy.getGender().getNumber());
                    ps.setString(6, houseCaddy.getTeam());
                    ps.setInt(7, houseCaddy.getTeamRole().getNumber());
                    ps.setString(8, houseCaddy.getCareer());
                    ps.setString(9, houseCaddy.getAddress());
                    ps.setString(10, houseCaddy.getAddressDetail());
                    ps.setString(11, integerToStringConverter.convertToDatabaseColumn(houseCaddy.getOffPart()));
                    ps.setString(12, convertHoliday(houseCaddy.getHoliday()));
                    ps.setDate(13, java.sql.Date.valueOf(houseCaddy.getBirth()));
                }

                @Override
                public int getBatchSize() {
                    return houseCaddyList.size();
                }
            });
    }

    /**
     * 휴무일을 0,1,2 형태의 String으로 변환
     * //TODO: Attribute Converter로 이동 필요
     */
    private String convertHoliday(List<Days> holiday) {
        return  holiday.stream()
                .map(Days::getNumber)
                .collect(Collectors.joining(","));
    }
}
