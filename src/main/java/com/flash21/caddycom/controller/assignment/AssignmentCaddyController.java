package com.flash21.caddycom.controller.assignment;

import com.flash21.caddycom.dto.Message;
import com.flash21.caddycom.dto.PagingResponse;
import com.flash21.caddycom.dto.assignment.AssignmentResponse;
import com.flash21.caddycom.entity.schedule.AssignmentStatus;
import com.flash21.caddycom.service.assignment.AssignmentCaddyService;
import com.flash21.caddycom.service.assignment.AutoAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Tag(name = "5-2. Assignment Caddy", description = "배정정보 (배정 후) API")
@RequestMapping("/api/assignments/caddy")
public class AssignmentCaddyController {
    private final AssignmentCaddyService assignmentCaddyService;
    private final AutoAssignmentService autoAssignmentService;


    @GetMapping("{golfFieldId}/{date}")
    @Operation(summary = "배정 결과 조회 API", description = "캐디 배정 후 결과를 페이징 조회한다." +
            "\n\ncourseId는 제외하거나 0을 넣으면 전체코스가 조회되고, status는 제외하거나 아무것도 넣지 않으면 전체 Status가 조회된다.")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PagingResponse<AssignmentResponse.Info>> getAssignments(
            @PathVariable Long golfFieldId,
            @PathVariable LocalDate date,
            @RequestParam(required = false, defaultValue = "0") Long courseId,
            @RequestParam(required = false) AssignmentStatus status,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok().body(assignmentCaddyService.getAssignments(golfFieldId, date, courseId, status, page));
    }


    @GetMapping("switch/{golfFieldId}/{date}")
    @Operation(summary = "배정 변경 가능한 캐디 목록 조회 API", description = "변경 가능한 캐디의 목록을 페이징 조회한다." +
            "\n\ncourseId와 part는 제외하거나 0을 넣으면 전체가 조회된다.")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PagingResponse<AssignmentResponse.Info>> getSwitchingCaddy(
            @PathVariable Long golfFieldId,
            @PathVariable LocalDate date,
            @RequestParam Long assignmentId,
            @RequestParam(required = false, defaultValue = "0") Long courseId,
            @RequestParam(required = false, defaultValue = "0") Integer part,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok().body(assignmentCaddyService.getSwitchingCaddy(golfFieldId, date, assignmentId, courseId, part, page));
    }


    @GetMapping("/detail")
    @Operation(summary = "배정 상세 조회 API", description = "캐디 배정 상세 정보를 조회한다.")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AssignmentResponse.Detail> getAssignmentDetail(@RequestParam Long assignmentId) {
        return ResponseEntity.ok().body(assignmentCaddyService.getAssignmentDetail(assignmentId));
    }


    @PatchMapping("/cancel")
    @Operation(summary = "배정 취소 API", description = "취소 요청된 배정을 취소 / 골프장 관리자가 직접 취소")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Message> cancelAssignment(@RequestParam Long assignmentId,
                                                    @RequestBody(required = false) String reason) {
        assignmentCaddyService.cancelAssignment(assignmentId, reason);
        return ResponseEntity.ok().body(new Message("배정이 취소되었습니다."));
    }


    @PatchMapping("/switch")
    @Operation(summary = "배정 변경 API", description = "두 캐디간 배정을 변경한다.")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Message> switchAssignment(@RequestParam Long fromId, @RequestParam Long toId) {
        assignmentCaddyService.switchAssignment(fromId, toId);
        return ResponseEntity.ok().body(new Message("배정이 변경되었습니다."));
    }

    @PostMapping("{golfFieldId}/{date}")
    @Operation(summary = "캐디 자동 배정", description = "해당 날짜의 스케줄들에 캐디를 배정한다" +
            "\n\n골프장에 소속된 하우스 캐디들을 조회하고, 휴일과 오프 파트를 체크하며 배정한다." +
            "\n\n마지막으로 배정된 캐디의 다음 사람을 골프장 필드에 ID로 기입, 다음 자동 배정에 그 캐디부터 자동 배정을 시작하도록 함")
    public ResponseEntity<Void> assignCaddyToSchedule(
            @PathVariable("golfFieldId") Long golfFieldId,
            @PathVariable("date") LocalDate date
    ) {
        autoAssignmentService.assignCaddyAutomatically(golfFieldId, date);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "캐디 선택 배정", description = "골프장 관리자가 해당 시간대의 배정 정보에 캐디를 직접 설정합니다.")
    @PatchMapping("/{assignmentsId}/{caddyId}")
    public ResponseEntity<Void> assignSelectedCaddy(@PathVariable Long assignmentsId,
                                                    @PathVariable Long caddyId
    ) {
        assignmentCaddyService.assignSelectedCaddy(assignmentsId, caddyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
