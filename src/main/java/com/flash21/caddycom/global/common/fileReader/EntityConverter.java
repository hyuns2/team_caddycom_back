package com.flash21.caddycom.global.common.fileReader;

import com.flash21.caddycom.entity.caddy.Days;
import com.flash21.caddycom.entity.caddy.Gender;
import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.entity.caddy.TeamRole;
import com.flash21.caddycom.entity.caddy.converter.IntegerToStringConverter;
import com.flash21.caddycom.entity.golfField.GolfField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EntityConverter {
    private final CellValueConverter cellValueConverter;
    private final IntegerToStringConverter integerToStringConverter;

    public HouseCaddy toEntity(List<String> tableList) {
        String type = tableList.get(0);
        String name = tableList.get(1);
        String phoneNumber = tableList.get(2);
        String team = tableList.get(3);
        TeamRole teamRole = cellValueConverter.convertTeamRole(tableList.get(4));
        Gender gender = cellValueConverter.convertGender(tableList.get(5));
        List<Days> holiday = cellValueConverter.convertHoliday(tableList.get(6));
        LocalDate birth = cellValueConverter.convertBirth(tableList.get(7));
        String career = tableList.get(8);
        String address = tableList.get(9);
        String addressDetail = tableList.get(10);
        String offPart = tableList.get(11);


        return HouseCaddy.builder()
                .caddyType(type)
                .name(name)
                .phoneNumber(phoneNumber)
                .gender(gender)
                .team(team)
                .teamRole(teamRole)
                .holiday(holiday)
                .birth(birth)
                .career(career)
                .address(address)
                .addressDetail(addressDetail)
                .offPart(integerToStringConverter.convertToEntityAttribute(offPart))
                .build();
    }
}
