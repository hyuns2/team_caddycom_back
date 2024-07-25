package com.flash21.caddycom.global.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flash21.caddycom.global.exception.ExceptionDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        if (request.getAttribute("exception") != null) {
            response.setContentType("application/json; charset=UTF-8");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            ObjectMapper objectMapper = new ObjectMapper();
            ExceptionDto exceptionDto = ExceptionDto.fail(HttpStatus.UNAUTHORIZED, request.getAttribute("exception").toString());
            objectMapper.writeValue(response.getOutputStream(), exceptionDto);
        }

    }
}
