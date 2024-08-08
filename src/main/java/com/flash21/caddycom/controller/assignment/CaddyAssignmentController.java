package com.flash21.caddycom.controller.assignment;

import com.flash21.caddycom.dto.Message;
import com.flash21.caddycom.dto.assignment.AssignmentRequest;
import com.flash21.caddycom.dto.assignment.AssignmentResponse;
import com.flash21.caddycom.service.assignment.AssignmentCaddyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "6. Caddy Assignment", description = "캐디 측의 배정 관련 API(하우스, 프리 공통 사용)")
@RequestMapping("/api/caddy/assignment")
public class CaddyAssignmentController {
    private final AssignmentCaddyService assignmentCaddyService;

    @PatchMapping("/cancel")
    @Operation(summary = "배정 취소요청 API", description = "하우스 캐디 - 취소 사유를 포함한 배정 취소 요청")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Message> cancelAssignment(@RequestParam Long assignmentId,
                                                    @RequestBody(required = false) AssignmentRequest.Cancel request) {
        assignmentCaddyService.requestCancelAssignment(assignmentId, request);
        return ResponseEntity.ok()
                .body(new Message("배정 취소가 요청 되었습니다."));
    }


    @GetMapping("/info/{caddyId}")
    @Operation(summary = "업무 시작 전 배정 상세 정보 확인 API", description = "공용 - 배정 상세 정보를 확인하고 캐디업무 시작 버튼을 누르는 화면")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<AssignmentResponse.CaddyAssignmentInfoDetail>> getAssignmentInfo(
            @RequestParam List<Long> assignmentIds
    ) {
        return ResponseEntity.ok()
                .body(assignmentCaddyService.getAssignmentInfo(assignmentIds));
    }
}
