package com.flash21.caddycom.entity.caddy;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {
    MALE(0),
    FEMALE(1);

    private final int number;
}
