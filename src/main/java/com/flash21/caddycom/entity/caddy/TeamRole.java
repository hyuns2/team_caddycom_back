package com.flash21.caddycom.entity.caddy;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TeamRole {
    LEADER(0),
    MEMBER(1);

    private final int number;
}
