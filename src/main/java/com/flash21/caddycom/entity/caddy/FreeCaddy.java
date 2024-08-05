package com.flash21.caddycom.entity.caddy;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
    private List<MatchedFreeCaddy> matchedFreeCaddyList;
}
