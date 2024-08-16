package com.flash21.caddycom.entity.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AssignmentStatus {
    NOTHING(0),
    BLOCKED(1),
    CANCELED(2),
    ASSIGNED(3),
    REQUESTED(4),
    DELETED(5);

    private final int number;
}
