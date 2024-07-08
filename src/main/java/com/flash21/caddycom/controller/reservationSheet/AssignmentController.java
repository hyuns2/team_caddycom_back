package com.flash21.caddycom.controller.reservationSheet;

import com.flash21.caddycom.dto.reservationSheet.AssignmentDto;
import com.flash21.caddycom.dto.reservationSheet.ReservationSheetDto;
import com.flash21.caddycom.service.reservationSheet.AssignmentService;
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
@Tag(name = "3-2. Reservation Sheet", description = "예약시트 API")
@RequestMapping("/api/assignments")
public class AssignmentController {
    final AssignmentService assignmentService;

    @Operation(summary = "배정정보 조회", description = "배정정보가 존재하지 않으면 생성한 후, 배정정보를 반환합니다.")
    @GetMapping("/{reservationSheetId}/{targetDate}")
    public ResponseEntity<List<AssignmentDto.AssignmentsResponseDto>> retrieveAssignments(@PathVariable Long reservationSheetId, @PathVariable LocalDate targetDate) {
        List<AssignmentDto.AssignmentsResponseDto> assignments = assignmentService.retrieveAssignments(reservationSheetId, targetDate);

        return new ResponseEntity<>(assignments, HttpStatus.OK);
    }
}
