package com.flash21.caddycom.global.jwt;

import com.flash21.caddycom.entity.account.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtClaims {
    private String phoneNumber;
    private Role role;
    private Long id;
}
