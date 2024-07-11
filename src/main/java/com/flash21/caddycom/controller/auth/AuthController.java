package com.flash21.caddycom.controller.auth;

import com.flash21.caddycom.dto.auth.SigninResponse;
import com.flash21.caddycom.dto.auth.SigninRequest;
import com.flash21.caddycom.service.auth.AuthService;
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

    @Operation(summary="웹 로그인(전체 시스템 관리자/골프장 관리자) API", description="전체 시스템 관리자 or 골프장 관리자 로그인")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/sign-in")
    public ResponseEntity<SigninResponse> signin(@Valid @RequestBody SigninRequest request){
        SigninResponse response = authService.login(request);
        return ResponseEntity.ok().body(response);
    }

}
