package com.flash21.caddycom.entity.caddy;

import com.flash21.caddycom.dto.caddy.HouseCaddyRequest;
import com.flash21.caddycom.entity.caddy.converter.DayListConverter;
import com.flash21.caddycom.entity.caddy.converter.PartListConverter;
import com.flash21.caddycom.entity.golfField.GolfField;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue(value = "H")
public class HouseCaddy extends Caddy {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private GolfField golfField;

    @Convert(converter = DayListConverter.class)
    private List<Days> holiday;

    @Convert(converter = DayListConverter.class)
    private List<Days> changedHoliday;

    @Convert(converter = PartListConverter.class)
    private List<Integer> offPart;

    private String address;

    private String addressDetail;

    private String team;

    private TeamRole teamRole;

    private String caddyType;

    public void updateHouseCaddyByManager(String team,
                                          TeamRole teamRole,
                                          List<Days> holiday,
                                          List<Integer> offPart,
                                          Gender gender,
                                          String birth,
                                          String address,
                                          String addressDetail,
                                          String career) {
        this.team = team;
        this.teamRole = teamRole;
        this.holiday = holiday;
        this.offPart = offPart;
        this.gender = gender;
        this.birth = LocalDate.parse(birth);
        this.address = address;
        this.addressDetail = addressDetail;
        this.career = career;
    }

    public void update(String profileUrl, List<Days> changedHoliday, String birth, String address, String addressDetail, String career) {
        if (profileUrl != null)
            this.profileUrl = profileUrl;
        if (changedHoliday != null)
            this.changedHoliday = changedHoliday;
        if (birth != null && !birth.isEmpty())
            this.birth = LocalDate.parse(birth);
        if (address != null && !address.isEmpty())
            this.address = address;
        if (addressDetail != null && !addressDetail.isEmpty())
            this.addressDetail = addressDetail;
        if (career != null && !career.isEmpty())
            this.career = career;
    }

    public void setTeamRole(TeamRole teamRole) {
        this.teamRole = teamRole;
    }

    public void updateHoliday() {
        this.holiday = new ArrayList<>(changedHoliday);
        this.changedHoliday = null;
    }

    public void setHoliday(List<Days> holiday) {
        this.holiday = holiday;
    }

}
