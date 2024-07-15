package com.flash21.caddycom.controller.reservationSheet;

import com.flash21.caddycom.dto.reservationSheet.ReservationSheetDto;
import com.flash21.caddycom.service.reservationSheet.ReservationSheetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "3-1. Reservation Sheet", description = "예약시트 API")
@RequestMapping("/api/reservation-sheet")
public class ReservationSheetController {
    final ReservationSheetService rsService;

    @Operation(summary = "예약시트 등록", description = "골프장 관리자가 예약시트를 등록합니다.")
    @PostMapping
    public ResponseEntity<Long> createReservationSheet(@AuthenticationPrincipal User user, @Valid @RequestBody ReservationSheetDto.CreateRequest dto) {
        Long reservationSheetInfoId = rsService.createReservationSheet(dto);

        return new ResponseEntity<>(reservationSheetInfoId, HttpStatus.CREATED);
    }

    @Operation(summary = "캘린더 메타정보 조회", description = "골프장 관리자가 캘린더에 표기되는 메타정보를 조회합니다.")
    @GetMapping("/calendar/{year}/{month}")
    public ResponseEntity<?> getMetaData(@AuthenticationPrincipal User user, @PathVariable int year, @PathVariable int month, @RequestParam List<Long> reservationSheetIdList) {
        List<ReservationSheetDto.MetaDataResponse> responseDtoList = rsService.getMetaData(year, month, reservationSheetIdList);

        return new ResponseEntity<>(responseDtoList, HttpStatus.OK);
    }
}
