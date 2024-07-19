package com.flash21.caddycom.entity.account;

import com.flash21.caddycom.entity.golfField.GolfField;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Manager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    private String password;

    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    private GolfField golfField;
}
