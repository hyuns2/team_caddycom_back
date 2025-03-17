package com.flash21.caddycom.controller.schedule;

import com.flash21.caddycom.dto.schedule.ScheduleResponse;
import com.flash21.caddycom.service.schedule.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "4-2. Reservation Sheet Schedule", description = "예약시트 스케줄 (캘린더) API")
@RequestMapping("/api/calendar")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(summary = "캘린더 메타정보 조회", description = "골프장 관리자가 캘린더에 표기되는 메타정보를 조회합니다.")
    @GetMapping("/{golfFieldId}/{year}/{month}")
    public ResponseEntity<List<ScheduleResponse.MetaData>> getMetaData(@PathVariable Long golfFieldId, @PathVariable int year, @PathVariable int month) {
        return new ResponseEntity<>(scheduleService.getMetaData(golfFieldId, year, month), HttpStatus.OK);
    }

    @Operation(summary = "배정정보 조회 및 생성", description = """
                골프장 관리자가 배정정보를 조회합니다.
                - 해당 날짜에 이미 배정정보가 존재하는 경우, 그 정보를 반환합니다.
                - 정보가 존재하지 않는 경우, 해당 날짜의 모든 배정정보를 생성한 후 반환합니다.
                반환형태 ex) { 코스: [A, B],
                           시간: [~~~],
                           A: [ {id&상태}, null, ~~ ],
                           B: [~~~] }
                - 인덱스 기준으로 하나의 타임에 해당합니다.
                - 어떤 코스가 어느 시간에는 배정정보를 갖고있지 않으면, null이 들어갑니다.
            """)
    @PostMapping("/{golfFieldId}/{date}")
    public ResponseEntity<Map<String, List<Object>>> getAssignments(@PathVariable Long golfFieldId, @PathVariable LocalDate date) {
        return new ResponseEntity<>(scheduleService.getAssignments(golfFieldId, date), HttpStatus.OK);
    }

    @Operation(summary = "캐디 캘린더 배정 정보 조회", description = "캐디가 캘린더에 표기되는 배정 메타정보를 조회합니다.")
    @GetMapping("/caddy/{caddyId}/{year}/{month}")
    public ResponseEntity<Map<LocalDate, List<ScheduleResponse.CaddyAssignmentInfo>>> getCaddyAssignments(@PathVariable Long caddyId, @PathVariable int year, @PathVariable int month) {
        return new ResponseEntity<>(scheduleService.getCaddyAssignments(caddyId, year, month), HttpStatus.OK);
    }
}
