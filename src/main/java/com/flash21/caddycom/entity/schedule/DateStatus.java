package com.flash21.caddycom.entity.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DateStatus {
    NOTHING(0),
    SETTING(1),
    ASSIGNED(2);

    private final int number;
}
