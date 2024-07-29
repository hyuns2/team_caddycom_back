package com.flash21.caddycom.controller.auth;


import com.flash21.caddycom.dto.Message;
import com.flash21.caddycom.dto.auth.SigninRequest;
import com.flash21.caddycom.dto.auth.SigninResponse;
import com.flash21.caddycom.service.auth.CaddyAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "0-2. Caddy Auth", description = "로그인/회원가입 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/caddy")
public class CaddyAuthController {

    private final CaddyAuthService caddyAuthService;

    @Operation(summary="하우스 캐디 첫번째 로그인 API",
            description="하우스 캐디의 최초 로그인/회원가입 시 사용 \n 골프장 등록 후 캐디 등록 (즉, waiting 상태가 없다.) ")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/sign-in/first")
    public ResponseEntity<SigninResponse.Caddy> firstSignin(@Valid @RequestBody SigninRequest.First request){
        SigninResponse.Caddy response = caddyAuthService.firstLogin(request);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary="하우스 캐디 이후 로그인 API", description="하우스 캐디의 최초 이후 로그인/회원가입 시 사용")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/sign-in")
    public ResponseEntity<SigninResponse.Caddy> signin(@Valid @RequestBody SigninRequest.Caddy request){
        SigninResponse.Caddy response = caddyAuthService.afterLogin(request);
        return ResponseEntity.ok().body(response);
    }


    @Operation(summary="하우스 캐디 비밀번호 설정 API", description="하우스 캐디 비밀번호 설정")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/sign-in/password")
    public ResponseEntity<Message> setPassword(@Valid @RequestBody SigninRequest.Password request){
        caddyAuthService.setPassword(request);
        return ResponseEntity.ok().body(new Message("비밀번호가 설정되었습니다."));
    }
}
