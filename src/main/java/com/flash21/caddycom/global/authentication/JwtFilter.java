package com.flash21.caddycom.global.authentication;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
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

        try {
            String jwt = jwtValidator.getAccessToken(jwtHeader);
            Claims claims = jwtValidator.extractClaims(jwt);

            ObjectMapper mapper = new ObjectMapper();
            JwtClaims jwtClaims = mapper.convertValue(claims.get("jwtClaims"), JwtClaims.class);

            JwtUserDetail jwtUserDetail = new JwtUserDetail(jwtClaims.getPhoneNumber(), jwtClaims.getRole());

            // jwt 서명이 정상이면 Authentication객체를 만듦.
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(jwtUserDetail, null, jwtUserDetail.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (RuntimeException e) {
            request.setAttribute("exception", e.getMessage());
        }

        filterChain.doFilter(request,response);
    }
}
