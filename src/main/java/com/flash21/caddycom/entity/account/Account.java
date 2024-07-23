package com.flash21.caddycom.entity.account;

import com.flash21.caddycom.entity.golfField.GolfField;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    private String password;

    private Role role;

    private LocalDate enteringDate;

    private String address;

    private String position;

    @ManyToOne(fetch = FetchType.LAZY)
    private GolfField golfField;

    /** 임시 토큰 저장소 */
    private String refreshToken;

    public void updatePassword(String password){
        this.password = password;
    }

    public void encodePassword(String password){
        this.password = password;
    }

    public void updateToken(String refreshToken){
        this.refreshToken = refreshToken;
    }

    public void linkGolfField(GolfField golfField){
        this.golfField = golfField;
    }
}
