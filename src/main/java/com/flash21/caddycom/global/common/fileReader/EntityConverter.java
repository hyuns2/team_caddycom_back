package com.flash21.caddycom.global.common.fileReader;

import com.flash21.caddycom.entity.caddy.Days;
import com.flash21.caddycom.entity.caddy.Gender;
import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.entity.caddy.TeamRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EntityConverter {
    private final CellValueConverter cellValueConverter;

    /**
     * List<String> 형태의 input 을 HouseCaddy Entity 로 변환
     * TODO: HouseCaddy 에 종속적이므로 디렉토리 이동 필요
     */

    public HouseCaddy toEntity(List<String> tableList) {
        String type = tableList.get(0);
        String name = tableList.get(1);
        String phoneNumber = cellValueConverter.convertPhoneNumber(tableList.get(2));
        String team = tableList.get(3);
        TeamRole teamRole = cellValueConverter.convertTeamRole(tableList.get(4));
        Gender gender = cellValueConverter.convertGender(tableList.get(5));
        List<Days> holiday = cellValueConverter.convertHoliday(tableList.get(6));
        LocalDate birth = cellValueConverter.convertBirth(tableList.get(7));
        String career = tableList.get(8);
        String address = tableList.get(9);
        String addressDetail = tableList.get(10);
        List<Integer> offPart = cellValueConverter.convertPart(tableList.get(11));


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
                .offPart(offPart)
                .build();
    }
}
