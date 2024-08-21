package com.flash21.caddycom.controller.assignment;

import com.flash21.caddycom.dto.Message;
import com.flash21.caddycom.dto.assignment.AssignmentRequest;
import com.flash21.caddycom.dto.assignment.AssignmentResponse;
import com.flash21.caddycom.service.schedule.AssignmentService;
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
@Tag(name = "5-1. Assignment", description = "배정정보 (배정 전) API")
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    //TODO: 배정정보를 생성하는 책임도 담당하기 때문에 Post 메서드로 변경 필요
    @Operation(summary = "배정정보 조회", description = """
                골프장 관리자가 배정정보를 조회합니다.
                - 해당 날짜에 이미 배정정보가 존재하는 경우, 그 정보를 반환합니다.
                - 정보가 존재하지 않는 경우, 해당 날짜의 모든 배정정보를 생성한 후 반환합니다.
                - 페이지는 0번부터 시작하고, 한 페이지당 10개 타임의 코스별 배정정보를 반환합니다.
                반환형태 ex) { 코스: [A, B],
                           시간: [~~~],
                           A: [ {id&상태}, null, ~~ ],
                           B: [~~~] }
                - 인덱스 기준으로 하나의 타임에 해당합니다.
                - 어떤 코스가 어느 시간에는 배정정보를 갖고있지 않으면, null이 들어갑니다.
            """)
    @GetMapping("/{golfFieldId}/{targetDate}/{page}")
    public ResponseEntity<Map<String, List<Object>>> getAssignments(@PathVariable Long golfFieldId,
                                                                    @PathVariable LocalDate targetDate,
                                                                    @PathVariable int page
    ) {
        Map<String, List<Object>> result = assignmentService.getAssignments(golfFieldId, targetDate, page);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "블락 조회", description = "골프장 관리자가 해당 시간대의 배정 정보에 블락을 조회합니다." +
            "\n블락 후 메세지를 기입했다면 메시지를 반환, 기입하지 않았다면 기본 메시지 반환")
    @GetMapping("/{assignmentsId}")
    public ResponseEntity<AssignmentResponse.Block> setBlock(@PathVariable Long assignmentsId
    ) {
        AssignmentResponse.Block result = assignmentService.getBlock(assignmentsId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "블락 설정", description = "골프장 관리자가 해당 시간대의 배정 정보에 블락을 설정합니다." +
            "\n배정 상태를 블락으로 바꾸고 요청한 메시지를 설정함")
    @PatchMapping("/{assignmentsId}")
    public ResponseEntity<Void> setBlock(@PathVariable Long assignmentsId,
                                         @RequestBody AssignmentRequest.Block blockRequest
    ) {
        assignmentService.setBlock(assignmentsId, blockRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "블락 취소", description = "골프장 관리자가 해당 시간대의 배정 정보에 블락을 취소합니다." +
            "\n배정 상태의 블락을 해제하고 메시지를 삭제함")
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
