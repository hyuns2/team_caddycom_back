package com.flash21.caddycom.entity.caddy;

import com.flash21.caddycom.entity.account.Role;
import com.flash21.caddycom.entity.caddy.converter.DayListConverter;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.reservationSheet.Assignment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
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

    private LocalDate birth;

    private String address;

    private String addressDetail;

    private String team;

    private String teamRole;

    private Long point;

    private Role role;


    public void updatePassword(String password) {
        this.password = password;
    }
}
