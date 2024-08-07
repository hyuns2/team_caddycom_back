package com.flash21.caddycom.entity.caddy;

import com.flash21.caddycom.entity.account.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "CTYPE")
public abstract class Caddy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(nullable = false)
    protected String name;

    @Column(nullable = false, unique = true)
    protected String phoneNumber;

    protected String password;

    protected Gender gender;

    protected LocalDate birth;

    protected String career;

    protected Long point;

    protected String refreshToken;

    protected Role role;

    protected String profileUrl;

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void encodePassword(String password){
        this.password = password;
    }
}
