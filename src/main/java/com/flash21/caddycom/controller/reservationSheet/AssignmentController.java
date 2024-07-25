package com.flash21.caddycom.controller.reservationSheet;

import com.flash21.caddycom.dto.reservationSheet.AssignmentDto;
import com.flash21.caddycom.service.reservationSheet.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "3-2. Reservation Sheet Assignment", description = "예약시트 배정정보 API")
@RequestMapping("/api/assignments")
public class AssignmentController {
    final AssignmentService assignmentService;

    @Operation(summary = "배정정보 조회", description = "골프장 관리자가 배정정보를 조회합니다.")
    @GetMapping("/{golfFieldId}/{targetDate}/{part}/{page}")
    public ResponseEntity<Map<String, Map<String, AssignmentDto.AssignmentsResponse>>> getAssignments(@AuthenticationPrincipal User user, @PathVariable Long golfFieldId, @PathVariable LocalDate targetDate, @PathVariable int page) {
        Map<String, Map<String, AssignmentDto.AssignmentsResponse>> result = assignmentService.getAssignments(golfFieldId, targetDate, page);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
