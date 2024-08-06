package com.flash21.caddycom.controller.assignment;

import com.flash21.caddycom.dto.Message;
import com.flash21.caddycom.dto.assignment.AssignmentRequest;
import com.flash21.caddycom.service.assignment.AssignmentCaddyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "5. House Caddy Assignment", description = "하우스 캐디 측의 배정 관련 API")
@RequestMapping("/api/house-caddy/assignment")
public class CaddyAssignmentController {
    private final AssignmentCaddyService assignmentCaddyService;

    @PatchMapping("/cancel")
    @Operation(summary = "배정 취소요청 API", description = "하우스 캐디 - 취소 사유를 포함한 배정 취소 요청")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Message> cancelAssignment(@RequestParam Long assignmentId,
                                                    @RequestBody(required = false) AssignmentRequest.Cancel request) {
        assignmentCaddyService.requestCancelAssignment(assignmentId, request);
        return ResponseEntity.ok().body(new Message("배정이 취소되었습니다."));
    }
}
