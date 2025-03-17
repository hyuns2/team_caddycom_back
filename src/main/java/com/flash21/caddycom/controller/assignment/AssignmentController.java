package com.flash21.caddycom.controller.assignment;

import com.flash21.caddycom.dto.Message;
import com.flash21.caddycom.dto.assignment.AssignmentRequest;
import com.flash21.caddycom.dto.assignment.AssignmentResponse;
import com.flash21.caddycom.service.assignment.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "5-1. Assignment", description = "배정정보 (배정 전) API")
@RequestMapping("/api/assignments")
public class AssignmentController {
    private final AssignmentService assignmentService;

    @Operation(summary = "블락 조회", description = "골프장 관리자가 해당 시간대의 배정 정보에 블락을 조회합니다." +
            "\n\n블락 후 메세지를 기입했다면 메시지를 반환, 기입하지 않았다면 기본 메시지 반환")
    @GetMapping("/{assignmentsId}")
    public ResponseEntity<AssignmentResponse.Block> setBlock(@PathVariable Long assignmentsId
    ) {
        AssignmentResponse.Block result = assignmentService.getBlock(assignmentsId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "블락 설정", description = "골프장 관리자가 해당 시간대의 배정 정보에 블락을 설정합니다." +
            "\n\n배정 상태를 블락으로 바꾸고 요청한 메시지를 설정함")
    @PatchMapping("/{assignmentsId}")
    public ResponseEntity<Void> setBlock(@PathVariable Long assignmentsId,
                                         @RequestBody AssignmentRequest.Block blockRequest
    ) {
        assignmentService.setBlock(assignmentsId, blockRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "블락 취소", description = "골프장 관리자가 해당 시간대의 배정 정보에 블락을 취소합니다." +
            "\n\n배정 상태의 블락을 해제하고 메시지를 삭제함")
    @PatchMapping("/{assignmentsId}/cancel")
    public ResponseEntity<Void> cancelBlock(@PathVariable Long assignmentsId
    ) {
        assignmentService.cancelBlock(assignmentsId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "외부 캐디 요청", description = "골프장 관리자가 해당 시간대의 배정 정보에 외부 캐디를 요청합니다.")
    @PatchMapping("/free-caddy")
    public ResponseEntity<Message> requestFreeCaddy(@RequestParam Long assignmentId) {
        assignmentService.requestFreeCaddy(assignmentId);
        return ResponseEntity.ok().body(new Message("외부 캐디 요청이 완료되었습니다."));
    }
}
