package com.flash21.caddycom.controller.golfFieldDetail;

import com.flash21.caddycom.dto.golfFieldDetail.formation.FormationRequest;
import com.flash21.caddycom.dto.golfFieldDetail.formation.FormationResponse;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import com.flash21.caddycom.service.golfFieldDetail.CourseService;
import com.flash21.caddycom.service.golfFieldDetail.FormationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@Tag(name = "2-1. Formation", description = "골프장 구성 설정 관련 API")
@RestController
@RequiredArgsConstructor
public class FormationController {
    private final FormationService formationService;
    private final GolfFieldRepository golfFieldRepository;
    private final CourseService courseService;

    @PostMapping("/api/formations")
    @Operation(summary = "골프장 구성 정보 생성 API")
    public ResponseEntity<Void> createFormation(@Valid @RequestBody FormationRequest.Process request) {
        GolfField golfField = golfFieldRepository.findById(request.getGolfFieldId())
                .orElseThrow(() -> new NoSuchElementException("해당 골프장은 존재하지 않습니다."));

        if(request.getCreate() != null)
            for(FormationRequest.Create createRequest : request.getCreate())
                formationService.createFormation(golfField, createRequest);

        if(request.getUpdate() != null)
            for(FormationRequest.Update updateRequest : request.getUpdate())
                formationService.updateFormation(updateRequest);

        if(!request.getDeleteFormations().isEmpty())
            formationService.deleteFormations(request.getDeleteFormations());
        if(!request.getDeleteCourses().isEmpty())
            courseService.deleteCourses(request.getDeleteCourses());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/api/formations")
    @Operation(summary = "골프장 구성 정보 반환 API")
    public ResponseEntity<List<FormationResponse.Create>> getFormations(Long golfFieldId) {
        List<FormationResponse.Create> formationInfos = formationService.getAllFormations(golfFieldId);

        return new ResponseEntity<>(formationInfos, HttpStatus.OK);
    }

}
