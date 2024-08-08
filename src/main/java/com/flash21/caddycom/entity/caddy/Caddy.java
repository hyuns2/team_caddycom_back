package com.flash21.caddycom.entity.caddy;

import com.flash21.caddycom.entity.account.Role;
import com.flash21.caddycom.entity.schedule.Assignment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

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

    protected String name;

    @Column(nullable = false, unique = true)
    protected String phoneNumber;

    protected String password;

    protected Gender gender;

    protected LocalDate birth;

    protected String career;

    protected Long point;

    protected Role role;

    protected String profileUrl;

    protected String refreshToken;

    @OneToMany(mappedBy = "caddy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignment> assignmentList;

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void encodePassword(String password) {
        this.password = password;
    }

    public HouseCaddy getHouseCaddy() {
        if (this instanceof HouseCaddy) {
            return (HouseCaddy) this;
        }
        return null;
    }

    public FreeCaddy getFreeCaddy() {
        if (this instanceof FreeCaddy) {
            return (FreeCaddy) this;
        }
        return null;
    }

    public Role getType() {
        if (this instanceof HouseCaddy) {
            return Role.ROLE_HOUSE_CADDY;
        }
        else if (this instanceof FreeCaddy) {
            return Role.ROLE_FREE_CADDY;
        }
        return null;
    }
}
