package com.flash21.caddycom.controller.assignment;

import com.flash21.caddycom.dto.PagingResponse;
import com.flash21.caddycom.dto.assignment.AssignmentResponse;
import com.flash21.caddycom.entity.reservationSheet.AssignmentStatus;
import com.flash21.caddycom.service.assignment.AssignmentCaddyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Tag(name = "4. Assignment Caddy", description = "배정정보 API")
@RequestMapping("/api/assignments/caddy")
public class AssignmentCaddyController {
    private final AssignmentCaddyService assignmentCaddyService;


    @GetMapping("{golfFieldId}/{date}/{courseId}/{status}/{page}")
    @Operation(summary="배정 결과 조회 API", description="캐디 배정 후 결과를 페이징 조회한다.")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PagingResponse<AssignmentResponse.Info>> getAssignments(
            @PathVariable Long golfFieldId,
            @PathVariable LocalDate date,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) AssignmentStatus status,
            @RequestParam(defaultValue = "0") int page)
    {
        return ResponseEntity.ok().body(assignmentCaddyService.getAssignments(golfFieldId, date, courseId, status, page));
    }

}
