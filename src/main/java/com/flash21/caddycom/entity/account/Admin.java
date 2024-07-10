package com.flash21.caddycom.entity.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Admin {
    @Id
    private String name = "관리자";

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Role role = Role.ROLE_ADMIN;
}
