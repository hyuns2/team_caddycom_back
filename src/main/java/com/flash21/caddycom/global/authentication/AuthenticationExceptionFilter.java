package com.flash21.caddycom.global.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flash21.caddycom.global.exception.ExceptionDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthenticationExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            sendError(HttpStatus.UNAUTHORIZED, response, e);
        }
    }

    public void sendError(HttpStatus status, HttpServletResponse response, Throwable ex) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json; charset=UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();

        ExceptionDto exceptionDto = ExceptionDto.fail(status, ex.getMessage());
        objectMapper.writeValue(response.getOutputStream(), exceptionDto);
    }
}
