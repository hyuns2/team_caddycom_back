package com.flash21.caddycom.controller.golfField;

import com.flash21.caddycom.dto.golfField.GolfFieldRequest;
import com.flash21.caddycom.service.golfField.GolfFieldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1. GolfField", description = "골프장 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/golf-field")
public class GolfFieldController {
    private final GolfFieldService golfFieldService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary="골프장 등록 API", description = "골프장 관리자 or 전체 시스템 관리자는 골프장을 등록한다.")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> create(@Valid @ModelAttribute GolfFieldRequest.Create request){
        golfFieldService.createGolfField(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    @Operation(summary = "골프장 삭제 API", description="전체 시스템 관리자는 골프장을 삭제한다.")
    public ResponseEntity<Void> delete(@RequestParam Long golfFieldId) {
        golfFieldService.deleteGolfField(golfFieldId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    @Operation(summary = "골프장 수정 API (구현 전, 뼈대만 있음)", description="")
    public ResponseEntity<Void> update(@RequestParam Long golfFieldId,
                                       @Valid @ModelAttribute GolfFieldRequest.AdditionalInfo request) {
        golfFieldService.updateGolfField(golfFieldId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @ResponseStatus(HttpStatus.OK)
    @PostMapping("additional-info")
    @Operation(summary = "골프장 추가정보 입력 API", description="골프장 관리자 or 전체 시스템 관리자는 골프장 추가정보를 입력한다.")
    public ResponseEntity<Void> addInfo(@RequestParam Long golfFieldId,
                                        @Valid @RequestBody GolfFieldRequest.AdditionalInfo request) {
        golfFieldService.addMoreInfo(golfFieldId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @ResponseStatus(HttpStatus.OK)
    @PostMapping("direction-info")
    @Operation(summary = "골프장 오는 길 안내 입력 API", description="골프장 관리자 or 전체 시스템 관리자는 골프장 오는 길 안내를 입력한다.")
    public ResponseEntity<Void> addDirectionInfo(@RequestParam Long golfFieldId,
                                                 @Valid @RequestBody GolfFieldRequest.DirectionsInfo request) {
        golfFieldService.addDirectionInfo(golfFieldId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping(value = "facility-info",consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "골프장 시설 안내 입력 API", description="골프장 관리자 or 전체 시스템 관리자는 골프장 시설 안내를 입력한다.")
    public ResponseEntity<Void> addFacilityInfo(@RequestParam Long golfFieldId,
                                                @Valid @ModelAttribute GolfFieldRequest.FacilityInfo request) {
        golfFieldService.addFacilityInfo(golfFieldId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
