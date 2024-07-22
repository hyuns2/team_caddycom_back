package com.flash21.caddycom.controller.auth;

import com.flash21.caddycom.dto.Message;
import com.flash21.caddycom.dto.auth.SigninResponse;
import com.flash21.caddycom.dto.auth.SigninRequest;
import com.flash21.caddycom.service.auth.AuthService;
import com.flash21.caddycom.service.auth.ManagerService;
import io.swagger.v3.oas.annotations.Hidden;
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
    @PostMapping("/web/sign-in")
    public ResponseEntity<SigninResponse.Web> WebSignin(@Valid @RequestBody SigninRequest.Web request){
        return ResponseEntity.ok().body(authService.login(request));
    }

    @Operation(summary="골프장 사장/직원 첫번째 로그인 API", description="골프장 사장/직원의 최초 로그인/회원가입 시 사용")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/sign-in/first")
    public ResponseEntity<SigninResponse.First> firstSignin(@Valid @RequestBody SigninRequest.First request){
        SigninResponse.First response = managerService.firstLogin(request);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary="골프장 사장/직원 이후 로그인 API", description="골프장 사장/직원의 최초 이후 로그인/회원가입 시 사용")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/sign-in")
    public ResponseEntity<SigninResponse.After> signin(@Valid @RequestBody SigninRequest.After request){
        SigninResponse.After response = managerService.afterLogin(request);
        return ResponseEntity.ok().body(response);
    }


    @Operation(summary="골프장 사장/직원 비밀번호 설정 API", description="골프장 사장/직원의 비밀번호 설정")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/sign-in/password")
    public ResponseEntity<Message> setPassword(@Valid @RequestBody SigninRequest.Password request){
        managerService.setPassword(request);
        return ResponseEntity.ok().body(new Message("비밀번호가 설정되었습니다."));
    }

}
