package com.flash21.caddycom.controller.assignment;

import com.flash21.caddycom.dto.Message;
import com.flash21.caddycom.dto.PagingResponse;
import com.flash21.caddycom.dto.assignment.AssignmentResponse;
import com.flash21.caddycom.entity.schedule.AssignmentStatus;
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


    @GetMapping("{golfFieldId}/{date}")
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


    @GetMapping("switch/{golfFieldId}/{date}")
    @Operation(summary="배정 결과 변경 API", description="변경 가능한 캐디의 목록을 페이징 조회한다.")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PagingResponse<AssignmentResponse.Info>> getSwitchingCaddy(
            @PathVariable Long golfFieldId,
            @PathVariable LocalDate date,
            @RequestParam Long assignmentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Integer part,
            @RequestParam(defaultValue = "0") int page)
    {
        return ResponseEntity.ok().body(assignmentCaddyService.getSwitchingCaddy(golfFieldId, date, assignmentId, courseId, part, page));
    }



    @GetMapping("/detail")
    @Operation(summary="배정 상세 조회 API", description="캐디 배정 상세 정보를 조회한다.")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AssignmentResponse.Detail> getAssignmentDetail(@RequestParam Long assignmentId) {
        return ResponseEntity.ok().body(assignmentCaddyService.getAssignmentDetail(assignmentId));
    }


    @PatchMapping("/cancel")
    @Operation(summary="배정 취소 API", description="취소 요청된 배정을 취소 / 골프장 관리자가 직접 취소")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Message> cancelAssignment(@RequestParam Long assignmentId,
                                                    @RequestBody(required = false) String reason){
        assignmentCaddyService.cancelAssignment(assignmentId, reason);
        return ResponseEntity.ok().body(new Message("배정이 취소되었습니다."));
    }


    @PatchMapping("/switch")
    @Operation(summary="배정 변경 API", description="두 캐디간 배정을 변경한다.")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Message> switchAssignment(@RequestParam Long fromId, @RequestParam Long toId) {
        assignmentCaddyService.switchAssignment(fromId, toId);
        return ResponseEntity.ok().body(new Message("배정이 변경되었습니다."));
    }

}
