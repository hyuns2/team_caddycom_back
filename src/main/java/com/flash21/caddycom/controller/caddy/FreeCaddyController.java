package com.flash21.caddycom.controller.caddy;

import com.flash21.caddycom.dto.Message;
import com.flash21.caddycom.dto.caddy.FreeCaddyCommand;
import com.flash21.caddycom.dto.caddy.FreeCaddyRequest;
import com.flash21.caddycom.dto.caddy.FreeCaddyResponse;
import com.flash21.caddycom.dto.golfField.GolfFieldResponse;
import com.flash21.caddycom.dto.schedule.ScheduleResponse;
import com.flash21.caddycom.service.caddy.FreeCaddyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "3-3. Free Caddy", description = "프리캐디 API")
@RequestMapping("/api/free-caddy")
public class FreeCaddyController {
    private final FreeCaddyService freeCaddyService;

    @Operation(summary = "프리 캐디 등록", description = "프리 캐디의 정보를 저장한다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> saveFreeCaddy(@Valid @ModelAttribute FreeCaddyRequest.Create request) {
        freeCaddyService.saveFreeCaddy(FreeCaddyCommand.Create.from(request));
        return ResponseEntity.ok().body(new Message("프리 캐디 등록 성공"));
    }


    @GetMapping
    @Operation(summary = "프리 캐디 조회", description = "프리 캐디의 정보를 조회한다.")
    public ResponseEntity<FreeCaddyResponse.Info> getFreeCaddy(@RequestParam Long caddyId) {
        return ResponseEntity.ok().body(freeCaddyService.getFreeCaddy(caddyId));
    }

    @GetMapping("/matched-golf-field")
    @Operation(summary = "프리 캐디 지정골프장 조회", description = "프리 캐디의 지정골프장을 조회한다.")
    public ResponseEntity<List<GolfFieldResponse.WithFreeCaddy>> getMatchedGolfField(@RequestParam Long caddyId) {
        return ResponseEntity.ok().body(freeCaddyService.getMatchedGolfField(caddyId));
    }

    @GetMapping("/matched-golf-field/assignments")
    @Operation(summary = "프리 캐디 지정골프장의 미배정 목록 조회", description = "프리 캐디의 지정골프장 하나에 대한 한달동안의 날짜별 미배정 목록을 조회한다.")
    public ResponseEntity<List<ScheduleResponse.NotAssigned>> getMatchedGolfFieldAssignments(
            @RequestParam Long golfFieldId,
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {
        return ResponseEntity.ok().body(freeCaddyService.getMatchedGolfFieldSchedule(golfFieldId, year, month));
    }
}
