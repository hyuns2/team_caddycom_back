package com.flash21.caddycom.controller.schedule;


import com.flash21.caddycom.dto.assignment.AssignmentResponse;
import com.flash21.caddycom.dto.schedule.ReservationSheetResponse;
import com.flash21.caddycom.service.schedule.ReservationSheetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "4-2. Reservation Sheet Schedule", description = "예약시트 스케줄(캘린더) API")
@RequestMapping("/api/reservation-sheet")
public class ScheduleController {

    private final ReservationSheetService rsService;

    @Operation(summary = "캘린더 메타정보 조회", description = "골프장 관리자가 캘린더에 표기되는 메타정보를 조회합니다.")
    @GetMapping("/calendar/{golfFieldId}/{year}/{month}")
    public ResponseEntity<List<ReservationSheetResponse.MetaData>> getMetaData(
            @PathVariable Long golfFieldId,
            @PathVariable int year,
            @PathVariable int month)
    {
        List<ReservationSheetResponse.MetaData> responseDtoList = rsService.getMetaData(golfFieldId, year, month);
        return new ResponseEntity<>(responseDtoList, HttpStatus.OK);
    }

    @Operation(summary = "캐디 캘린더 배정 정보 조회", description = "캐디가 캘린더에 표기되는 배정 메타정보를 조회합니다." +
            "\n\n배정된 일정의 부와 시간을 일 별로 보여줌")
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
