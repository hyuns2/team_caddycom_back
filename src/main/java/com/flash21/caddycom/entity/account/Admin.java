package com.flash21.caddycom.entity.account;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Admin {
    @Id
    private String id;
    private String name = "관리자";
    private String password;
    private Role role = Role.ROLE_ADMIN;
}
