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

    @Operation(summary = "예약시트 등록", description = """
                    골프장 관리자가 예약시트를 등록합니다.
                    - 이미 같은 날짜에 같은 코스가 예약된 경우, 등록할 수 없습니다.
                    - 시작날짜는 오늘 이후로만 가능하고, 종료날짜는 시작날짜 이전일 수 없습니다.
                    - 시작시간, 종료시간, 티오프간격은 각각 리스트로 받습니다.
                        1. 인덱스가 같으면 같은 부에 속합니다.
                        2. 각 리스트의 크기는 모두 동일해야 합니다.
                        3. 1부를 시작으로, 리스트 순서대로 몇 부인지 정의됩니다.
                """)
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

    @Operation(summary = "예약시트 수정", description = """
                    골프장 관리자가 특정 예약시트를 수정합니다.
                    - 수정하려는 예약시트에 블락, 배정, 취소요청 상태의 타임이 하나라도 있으면, 수정이 불가합니다.
                    - 위 상태의 타임을 모두 취소 상태로 변경하면, 수정이 가능해집니다.
                    - 현재시간 기준 이전의 스케줄과 배정정보 데이터는 히스토리로 보관하고, 이후만 수정됩니다.
                    - 현재시간 기준 이후의 캐디가 배정된 상태인 배정정보도 캐디에게 알리기 위해 히스토리로 보관합니다.
                """)
    @PatchMapping("/{reservationId}")
    public ResponseEntity<Void> updateReservationSheet(@PathVariable Long reservationId, @Valid @RequestBody ReservationSheetRequest.CreateOrUpdate dto) {
        rsService.updateReservationSheet(reservationId, dto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "예약시트 삭제", description = """
                    골프장 관리자가 특정 예약시트를 삭제합니다.
                    - 현재시간 기준 이전의 스케줄과 배정정보 데이터는 히스토리로 보관하고, 이후만 삭제됩니다.
                    - 현재시간 기준 이후의 캐디가 배정된 상태인 배정정보도 캐디에게 알리기 위해 히스토리로 보관합니다.
                """)
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservationSheet(@PathVariable Long reservationId) {
        rsService.deleteReservationSheet(reservationId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
