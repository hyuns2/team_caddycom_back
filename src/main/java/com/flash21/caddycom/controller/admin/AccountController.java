package com.flash21.caddycom.controller.admin;


import com.flash21.caddycom.dto.Message;
import com.flash21.caddycom.dto.account.AccountRequest;
import com.flash21.caddycom.dto.account.AccountResponse;
import com.flash21.caddycom.dto.auth.SigninRequest;
import com.flash21.caddycom.dto.auth.SigninResponse;
import com.flash21.caddycom.service.account.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "0. Account", description = "골프장 관리자(직원) 계정 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {
    private final AccountService accountService;

    @Operation(summary="직원 계정 생성 API", description="전체 시스템 관리자 or 골프장 관리자는 직원 데이터를 만들 수 있다.")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/employee")
    public ResponseEntity<Message> createAccount(@Valid @RequestParam Long golfFieldId,
                                                 @RequestBody List<AccountRequest.Create> request){
        accountService.createAccounts(golfFieldId, request);
        return ResponseEntity.ok().body(new Message("직원 계정 생성 완료"));
    }

    @Operation(summary="직원 계정 조회 API", description="전체 시스템 관리자 or 골프장 관리자는 직원 데이터를 조회할 수 있다.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/employee")
    public ResponseEntity<List<AccountResponse.Info>> getAccount(@Valid @RequestParam Long golfFieldId){
        return ResponseEntity.ok().body(accountService.getAccounts(golfFieldId));
    }
}
