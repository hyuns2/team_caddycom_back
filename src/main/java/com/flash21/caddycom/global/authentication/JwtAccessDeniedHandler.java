package com.flash21.caddycom.global.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flash21.caddycom.global.exception.ExceptionDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e)
            throws IOException, ServletException {
        response.setContentType("application/json; charset=UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();

        ExceptionDto exceptionDto = ExceptionDto.fail(HttpStatus.UNAUTHORIZED, request.getAttribute("exception").toString());
        objectMapper.writeValue(response.getOutputStream(), exceptionDto);
    }
}
