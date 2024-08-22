package com.flash21.caddycom.entity.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 예약시트 생성시 수백개의 assignment가 생기는데 지연 생성을 위해 assignment가 생겼는지 아닌지를 나타내는 enum
 */
@Getter
@AllArgsConstructor
public enum DateStatus {
    NOTHING(0),
    SETTING(1),
    ASSIGNED(2);

    private final int number;
}
