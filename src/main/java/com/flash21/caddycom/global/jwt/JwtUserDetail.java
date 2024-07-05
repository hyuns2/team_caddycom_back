package com.flash21.caddycom.global.jwt;

import com.flash21.caddycom.entity.account.Role;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import java.util.Collection;

@Hidden
@AllArgsConstructor
public class JwtUserDetail implements UserDetails {
    private String username;
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of((GrantedAuthority) () -> String.valueOf(role));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

}