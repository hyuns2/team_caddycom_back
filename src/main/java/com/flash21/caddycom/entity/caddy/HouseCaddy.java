package com.flash21.caddycom.entity.caddy;

import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.reservationSheet.Assignment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @OneToMany
    @JoinColumn
    private List<Assignment> assignmentList;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String contact;

    private String password;

    private Gender gender;

    private String holiday;

    private String birth;

    private String address;

    private String addressDetail;

    private String team;

    private String role;
}
