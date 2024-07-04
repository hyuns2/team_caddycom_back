package com.flash21.caddycom.controller;

import com.flash21.caddycom.dto.ReservationSheetDto;
import com.flash21.caddycom.service.ReservationSheetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "3. Reservation Sheet", description = "예약시트 API")
@RequestMapping("/api/reservation-sheet")
public class ReservationSheetController {
    final ReservationSheetService rsService;

    @Operation(summary = "예약시트 등록", description = "골프장이 예약시트를 등록합니다.")
    @PostMapping
    public ResponseEntity<?> createReservationSheet(@Valid @RequestBody ReservationSheetDto.CreateRequestDto dto) {
        rsService.createReservationSheet(dto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
