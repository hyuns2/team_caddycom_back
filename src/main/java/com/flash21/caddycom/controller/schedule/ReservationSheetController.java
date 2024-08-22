package com.flash21.caddycom.controller.schedule;

import com.flash21.caddycom.dto.schedule.ReservationSheetRequest;
import com.flash21.caddycom.dto.schedule.ReservationSheetResponse;
import com.flash21.caddycom.service.schedule.ReservationSheetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "4-1. Reservation Sheet", description = "예약시트 API")
@RequestMapping("/api/reservation-sheet")
public class ReservationSheetController {
    final ReservationSheetService rsService;

    @Operation(summary = "예약시트 등록", description = "골프장 관리자가 예약시트를 등록합니다.")
    @PostMapping
    public ResponseEntity<Void> createReservationSheet(@Valid @RequestBody ReservationSheetRequest.CreateOrUpdate dto) {
        rsService.createReservationSheet(dto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "예약시트 전체조회", description = "골프장 관리자가 전체 예약시트를 조회합니다.")
    @GetMapping("/{golfFieldId}")
    public ResponseEntity<List<ReservationSheetResponse.Get>> getReservationSheet(@PathVariable Long golfFieldId) {
        List<ReservationSheetResponse.Get> result = rsService.getReservationSheet(golfFieldId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "예약시트 수정", description = "골프장 관리자가 특정 예약시트를 수정합니다.")
    @PatchMapping("/{reservationId}")
    public ResponseEntity<Void> updateReservationSheet(@PathVariable Long reservationId, @Valid @RequestBody ReservationSheetRequest.CreateOrUpdate dto) {
        rsService.updateReservationSheet(reservationId, dto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "예약시트 삭제", description = "골프장 관리자가 특정 예약시트를 삭제합니다.")
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservationSheet(@PathVariable Long reservationId) {
        rsService.deleteReservationSheet(reservationId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
