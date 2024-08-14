package com.flash21.caddycom.controller.admin;


import com.flash21.caddycom.dto.Message;
import com.flash21.caddycom.dto.golfStaff.GolfStaffCommand;
import com.flash21.caddycom.dto.golfStaff.GolfStaffRequest;
import com.flash21.caddycom.dto.golfStaff.GolfStaffResponse;
import com.flash21.caddycom.service.account.GolfStaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "0-0. Golf Staff", description = "골프장 관리자(직원) 계정 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/golf-staff")
public class GolfStaffController {
    private final GolfStaffService golfStaffService;

    @Operation(summary="직원 계정 생성 API", description="전체 시스템 관리자 or 골프장 관리자는 직원 데이터를 만들 수 있다.")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/employee")
    public ResponseEntity<Message> createGolfStaff(@Valid @RequestParam Long golfFieldId,
                                                   @Valid @RequestBody List<GolfStaffRequest.Create> request){
        golfStaffService.createGolfStaff(golfFieldId, request.stream().map(GolfStaffCommand.Create::from).toList());
        return ResponseEntity.ok().body(new Message("직원 계정 생성 완료"));
    }

    @Operation(summary="직원 계정 조회 API", description="전체 시스템 관리자 or 골프장 관리자는 직원 데이터를 조회할 수 있다.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/employee")
    public ResponseEntity<List<GolfStaffResponse.Info>> getGolfStaff(@Valid @RequestParam Long golfFieldId){
        return ResponseEntity.ok().body(golfStaffService.getGolfStaff(golfFieldId));
    }
}