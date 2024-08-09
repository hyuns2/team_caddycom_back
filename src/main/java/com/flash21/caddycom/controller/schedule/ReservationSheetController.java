package com.flash21.caddycom.controller.schedule;

import com.flash21.caddycom.dto.assignment.AssignmentResponse;
import com.flash21.caddycom.dto.schedule.ReservationSheetDto;
import com.flash21.caddycom.service.schedule.ReservationSheetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "3-1. Reservation Sheet", description = "예약시트 API")
@RequestMapping("/api/reservation-sheet")
public class ReservationSheetController {
    final ReservationSheetService rsService;

    @Operation(summary = "예약시트 등록", description = "골프장 관리자가 예약시트를 등록합니다.")
    @PostMapping
    public ResponseEntity<Void> createReservationSheet(@Valid @RequestBody ReservationSheetDto.CreateOrUpdateRequest dto) {
        rsService.createReservationSheet(dto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "예약시트 전체조회", description = "골프장 관리자가 전체 예약시트를 조회합니다.")
    @GetMapping("/{golfFieldId}")
    public ResponseEntity<List<ReservationSheetDto.GetResponse>> getReservationSheet(@PathVariable Long golfFieldId) {
        List<ReservationSheetDto.GetResponse> result = rsService.getReservationSheet(golfFieldId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "예약시트 수정", description = "골프장 관리자가 특정 예약시트를 수정합니다.")
    @PatchMapping("/{reservationId}")
    public ResponseEntity<Void> updateReservationSheet(@PathVariable Long reservationId, @Valid @RequestBody ReservationSheetDto.CreateOrUpdateRequest dto) {
        rsService.updateReservationSheet(reservationId, dto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "예약시트 삭제", description = "골프장 관리자가 특정 예약시트를 삭제합니다.")
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservationSheet(@PathVariable Long reservationId) {
        rsService.deleteReservationSheet(reservationId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "캘린더 메타정보 조회", description = "골프장 관리자가 캘린더에 표기되는 메타정보를 조회합니다.")
    @GetMapping("/calendar/{golfFieldId}/{year}/{month}")
    public ResponseEntity<List<ReservationSheetDto.MetaDataResponse>> getMetaData(
            @PathVariable Long golfFieldId,
            @PathVariable int year,
            @PathVariable int month)
    {
        List<ReservationSheetDto.MetaDataResponse> responseDtoList = rsService.getMetaData(golfFieldId, year, month);
        return new ResponseEntity<>(responseDtoList, HttpStatus.OK);
    }

    @Operation(summary = "캐디 캘린더 배정 정보 조회", description = "캐디가 캘린더에 표기되는 배정 메타정보를 조회합니다.")
    @GetMapping("/calendar/caddy/{caddyId}/{year}/{month}")
    public ResponseEntity<Map<LocalDate, List<AssignmentResponse.CaddyAssignmentInfo>>> getCaddyAssignments(
            @PathVariable Long caddyId,
            @PathVariable int year,
            @PathVariable int month)
    {
        Map<LocalDate, List<AssignmentResponse.CaddyAssignmentInfo>> responseDtoList =
                rsService.getAssignmentResultSheet(caddyId, year, month);
        return new ResponseEntity<>(responseDtoList, HttpStatus.OK);
    }
}
