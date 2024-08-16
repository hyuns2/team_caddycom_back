package com.flash21.caddycom.entity.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    ROLE_ADMIN(0),
    ROLE_OWNER(1),
    ROLE_EMPLOYEE(2),
    ROLE_HOUSE_CADDY(3),
    ROLE_FREE_CADDY(4);

    private final int number;
}
