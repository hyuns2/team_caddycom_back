package com.flash21.caddycom.controller.auth;

import com.flash21.caddycom.dto.auth.SigninResponse;
import com.flash21.caddycom.dto.auth.SigninRequest;
import com.flash21.caddycom.service.auth.AuthService;
import com.flash21.caddycom.service.auth.ManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "0. Auth", description = "로그인/회원가입 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final ManagerService managerService;

    @Operation(summary="웹 로그인(전체 시스템 관리자/골프장 관리자) API", description="전체 시스템 관리자 or 골프장 관리자 로그인")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/sign-in")
    public ResponseEntity<SigninResponse.Web> signin(@Valid @RequestBody SigninRequest.Web request){
        return ResponseEntity.ok().body(authService.login(request));
    }

    @Operation(summary="골프장 사장/직원 첫번째 로그인 API", description="골프장 사장/직원의 최초 로그인/회원가입 시 사용")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/sign-in/first")
    public ResponseEntity<SigninResponse.First> firstSignin(@Valid @RequestBody SigninRequest.First request){
        SigninResponse.First response = managerService.firstLogin(request);
        return ResponseEntity.ok().body(response);
    }

}
