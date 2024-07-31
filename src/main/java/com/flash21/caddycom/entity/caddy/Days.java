package com.flash21.caddycom.entity.caddy;

import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public enum Days {
    MON(1),
    TUE(2),
    WED(3),
    THU(4),
    FRI(5),
    SAT(6),
    SUN(7);

    private final int number;
    private static final Map<Integer, Days> valueToDayMap = new HashMap<>();

    static {
        for (Days day : Days.values()) {
            valueToDayMap.put(day.number, day);
        }
    }

    public String getNumber() {
        return String.valueOf(number);
    }


    // TODO: 비지니스 레이어로 이동 필요
    public static Days fromNumber(String number) {
        Days day = valueToDayMap.get(Integer.parseInt(number));
        if(day == null) {
            throw new IllegalArgumentException("유효하지 않은 요일입니다: " + number);
        }
        return day;
    }
}
