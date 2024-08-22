package com.flash21.caddycom.controller.golfFieldDetail;

import com.flash21.caddycom.dto.golfFieldDetail.formation.FormationRequest;
import com.flash21.caddycom.dto.golfFieldDetail.formation.FormationResponse;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import com.flash21.caddycom.service.golfFieldDetail.FormationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Tag(name = "2-1. Course", description = "골프장 코스 정보(구성, 코스) 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/formations")
public class FormationController {
    private final FormationService formationService;

    @PostMapping
    @Operation(summary = "골프장 구성 정보 생성/수정 API", description = """
            골프장 관리자가 구성 정보를 생성/수정한다. <br>
            - 구성 정보에는 코스도 포함된다.
            - 생성 시 홀(Hole)과 티(Tee) 정보 또한 함께 생성된다.
            - 코스의 전체 홀 수 수정 시 그에 맞춰 홀 정보가 추가로 생성되거나 삭제된다.
            """)
    public ResponseEntity<Void> createFormation(@Valid @RequestBody FormationRequest.Process request) {
        formationService.processCreate(request);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "골프장 구성 정보 반환 API", description = "골프장에 포함된 모든 구성 정보를 반환한다.")
    public ResponseEntity<List<FormationResponse.Info>> getFormations(Long golfFieldId) {
        List<FormationResponse.Info> formationInfos = formationService.getAllFormations(golfFieldId);

        return new ResponseEntity<>(formationInfos, HttpStatus.OK);
    }

    @DeleteMapping
    @Operation(summary = "골프장 구성 정보 삭제 API", description = """
            골프장의 구성 정보를 삭제한다. 삭제 시 구성 정보에 포함된 코스 또한 같이 삭제된다.
            - 삭제되는 코스 중 블락되었거나 캐디가 배정된 미래 일정이 있을 경우 삭제에 실패한다.
            - 코스 삭제는 soft delete로 이루어진다.
            """)
    public ResponseEntity<Void> deleteFormations(@Valid @RequestBody FormationRequest.Delete request) {

        formationService.deleteFormations(request.getDeleteFormations());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
