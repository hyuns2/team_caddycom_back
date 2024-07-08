package com.flash21.caddycom.controller.reservationSheet;

import com.flash21.caddycom.dto.reservationSheet.ReservationSheetDto;
import com.flash21.caddycom.service.reservationSheet.ReservationSheetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "3-1. Reservation Sheet", description = "예약시트 API")
@RequestMapping("/api/reservation-sheet")
public class ReservationSheetController {
    final ReservationSheetService rsService;

    @Operation(summary = "예약시트 등록", description = "골프장이 예약시트를 등록합니다.")
    @PostMapping
    public ResponseEntity<?> createReservationSheet(@Valid @RequestBody ReservationSheetDto.CreateRequestDto dto) {
        List<Long> reservationSheetIdList = rsService.createReservationSheet(dto);

        return new ResponseEntity<>(reservationSheetIdList, HttpStatus.CREATED);
    }

    @Operation(summary = "캘린더 메타정보 조회", description = "캘린더에 표기되는 메타정보를 조회합니다.")
    @GetMapping("/calendar")
    public ResponseEntity<?> retrieveMetaData(@RequestParam LocalDate targetDate, @RequestParam List<Long> reservationSheetIdList) {
        List<ReservationSheetDto.MetaDataResponseDto> responseDtoList = rsService.retrieveMetaData(targetDate, reservationSheetIdList);

        return new ResponseEntity<>(responseDtoList, HttpStatus.OK);
    }
}
