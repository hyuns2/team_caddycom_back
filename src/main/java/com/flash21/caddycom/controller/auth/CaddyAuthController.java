package com.flash21.caddycom.controller.auth;


import com.flash21.caddycom.dto.Message;
import com.flash21.caddycom.dto.auth.SigninCommand;
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

    @Operation(summary="캐디 첫번째 로그인 API",
            description="캐디의 최초 로그인/회원가입 시 사용" +
                    "\n\n 골프장 등록 후 캐디 등록 (즉, waiting 상태가 없다.)" +
                    "\n\n 하우스 캐디는 사장님이 웹을 통해 미리 등록해야하며, 최조 접속시 DB에 저장된 정보가 없으면 모두 프리캐디로 간주함.")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/sign-in/first")
    public ResponseEntity<SigninResponse.CaddyMain> firstSignin(@Valid @RequestBody SigninRequest.First request){
        SigninResponse.CaddyMain response = caddyAuthService.firstLogin(SigninCommand.First.from(request));
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary="캐디 이후 로그인 API", description="캐디의 최초 이후 로그인/회원가입 시 사용")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/sign-in")
    public ResponseEntity<SigninResponse.CaddyMain> signin(@Valid @RequestBody SigninRequest.Caddy request){
        SigninResponse.CaddyMain response = caddyAuthService.afterLogin(SigninCommand.Caddy.from(request));
        return ResponseEntity.ok().body(response);
    }


    @Operation(summary="캐디 비밀번호 설정 API", description="캐디 비밀번호 설정")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/sign-in/password")
    public ResponseEntity<Message> setPassword(@Valid @RequestBody SigninRequest.Password request){
        caddyAuthService.setPassword(SigninCommand.Password.from(request));
        return ResponseEntity.ok().body(new Message("비밀번호가 설정되었습니다."));
    }
}
