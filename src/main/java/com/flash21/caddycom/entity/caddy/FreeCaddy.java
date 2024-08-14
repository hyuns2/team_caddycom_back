package com.flash21.caddycom.entity.caddy;


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
@DiscriminatorValue(value = "F")
public class FreeCaddy extends Caddy{

    private String intro;

    private String regions;

    @OneToMany(mappedBy = "freeCaddy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchedFreeCaddy> matchedFreeCaddyList = new ArrayList<>();


    public void create(String name, String phoneNumber, String regions, Gender gender, LocalDate birth, String career, String intro, List<MatchedFreeCaddy> matchedFreeCaddyList, String profileUrl) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.regions = regions;
        this.gender = gender;
        this.birth = birth;
        this.career = career;
        this.intro = intro;
        if (matchedFreeCaddyList != null && !matchedFreeCaddyList.isEmpty()) {
            for (MatchedFreeCaddy matchedFreeCaddy : matchedFreeCaddyList) {
                matchedFreeCaddy.setFreeCaddy(this);
                this.matchedFreeCaddyList.add(matchedFreeCaddy);
            }
        }
        this.profileUrl = profileUrl;
    }
}
