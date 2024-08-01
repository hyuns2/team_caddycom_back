package com.flash21.caddycom.entity.caddy;

import com.flash21.caddycom.dto.caddy.HouseCaddyRequestDto;
import com.flash21.caddycom.entity.account.Role;
import com.flash21.caddycom.entity.caddy.converter.DayListConverter;
import com.flash21.caddycom.entity.caddy.converter.IntegerToStringConverter;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.schedule.Assignment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HouseCaddy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private GolfField golfField;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn
    private List<Assignment> assignmentList;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phoneNumber;

    private String password;

    private Gender gender;

    @Convert(converter = DayListConverter.class)
    private List<Days> holiday;

    @Convert(converter = DayListConverter.class)
    private List<Days> changedHoliday;

    @Convert(converter = IntegerToStringConverter.class)
    private List<Integer> offPart;

    private LocalDate birth;

    private String address;

    private String addressDetail;

    private String team;

    private TeamRole teamRole;

    private String career;

    private Long point;

    private Role role;

    private String refreshToken;

    private String caddyType;


    public void updatePassword(String password) {
        this.password = password;
    }
    public void updateToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    public void updateHouseCaddy(HouseCaddyRequestDto.updateHouseCaddy dto) {
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
