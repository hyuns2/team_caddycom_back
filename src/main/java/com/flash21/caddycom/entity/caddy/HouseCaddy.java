package com.flash21.caddycom.entity.caddy;

import com.flash21.caddycom.dto.caddy.HouseCaddyRequestDto;
import com.flash21.caddycom.entity.caddy.converter.DayListConverter;
import com.flash21.caddycom.entity.caddy.converter.PartListConverter;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.schedule.Assignment;
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

    @OneToMany(mappedBy = "houseCaddy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignment> assignmentList;

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


    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateHouseCaddyByManager(HouseCaddyRequestDto.updateHouseCaddyByManager dto) {
        this.team = dto.getTeam();
        this.teamRole = dto.getTeamRole();
        this.holiday = dto.getHoliday();
        this.offPart = dto.getOffPart();
        this.gender = dto.getGender();
        this.birth = LocalDate.parse(dto.getBirth());
        this.address = dto.getAddress();
        this.addressDetail = dto.getAddressDetail();
        this.career = dto.getCareer();
    }

    public void update(String profileUrl, List<Days> changedHoliday, String birth, String address, String addressDetail, String career) {
        if (profileUrl != null)
            this.profileUrl = profileUrl;
        if (changedHoliday != null)
            this.changedHoliday = changedHoliday;
        if (birth != null && !birth.isEmpty())
            this.birth = LocalDate.parse(birth);
        if (address != null)
            this.address = address;
        if (addressDetail != null)
            this.addressDetail = addressDetail;
        if (career != null)
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

    public HouseCaddy attachGolfField(GolfField golfField) {
        this.golfField = golfField;
        return this;
    }
}
