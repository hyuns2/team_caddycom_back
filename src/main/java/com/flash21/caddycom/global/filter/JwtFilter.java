package com.flash21.caddycom.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flash21.caddycom.global.jwt.JwtClaims;
import com.flash21.caddycom.global.jwt.JwtUserDetail;
import com.flash21.caddycom.global.jwt.JwtValidator;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtValidator jwtValidator;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwtHeader = request.getHeader("Authorization");

        if (jwtHeader==null){
            filterChain.doFilter(request,response);
            return;
        }

        String jwt = jwtValidator.getAccessToken(jwtHeader);
        Claims claims = jwtValidator.extractClaims(jwt);
        ObjectMapper mapper = new ObjectMapper();
        JwtClaims jwtClaims = mapper.convertValue(claims.get("jwtClaims"), JwtClaims.class);


        JwtUserDetail jwtUserDetail = new JwtUserDetail(jwtClaims.getPhoneNumber(), jwtClaims.getRole());

        // jwt 서명이 정상이면 Authentication객체를 만듦.
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(jwtUserDetail, null, jwtUserDetail.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request,response);
    }
}
