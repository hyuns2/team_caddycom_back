package com.flash21.caddycom.controller.golfField;

import com.flash21.caddycom.dto.Message;
import com.flash21.caddycom.dto.golfField.FacilityRequest;
import com.flash21.caddycom.dto.golfField.FacilityResponse;
import com.flash21.caddycom.dto.golfField.GolfFieldRequest;
import com.flash21.caddycom.dto.golfField.GolfFieldResponse;
import com.flash21.caddycom.service.golfField.FacilityService;
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
    private final FacilityService facilityService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary="골프장 등록 API", description = "골프장 관리자 or 전체 시스템 관리자는 골프장을 등록한다.")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Message> create(@Valid @ModelAttribute GolfFieldRequest.Create request){
        golfFieldService.createGolfField(request);
        return ResponseEntity.ok().body(new Message("골프장이 등록되었습니다."));
    }


    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    @Operation(summary = "골프장 삭제 API", description="전체 시스템 관리자는 골프장을 삭제한다.")
    public ResponseEntity<Message> delete(@RequestParam Long golfFieldId) {
        golfFieldService.deleteGolfField(golfFieldId);
        return ResponseEntity.ok().body(new Message("골프장이 삭제되었습니다.."));
    }


    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    @Operation(summary = "골프장 수정 API", description="골프장 관리자 or 전체 시스템 관리자는 골프장 정보를 수정한다.")
    public ResponseEntity<Message> update(@RequestParam Long golfFieldId,
                                       @Valid @ModelAttribute GolfFieldRequest.Update request) {
        golfFieldService.updateGolfField(golfFieldId, request);
        return ResponseEntity.ok().body(new Message("골프장이 수정되었습니다."));
    }


    @ResponseStatus(HttpStatus.OK)
    @PostMapping("detail-info")
    @Operation(summary = "골프장 추가정보 입력 API", description="골프장 관리자 or 전체 시스템 관리자는 골프장 추가정보를 입력한다.")
    public ResponseEntity<Message> addInfo(@RequestParam Long golfFieldId,
                                        @Valid @RequestBody GolfFieldRequest.AdditionalInfo request) {
        golfFieldService.addMoreInfo(golfFieldId, request);
        return ResponseEntity.ok().body(new Message("골프장 추가정보가 등록되었습니다."));
    }


    @ResponseStatus(HttpStatus.OK)
    @PostMapping("direction-info")
    @Operation(summary = "골프장 오는 길 입력 API", description="골프장 관리자 or 전체 시스템 관리자는 골프장 오는 길 안내를 입력한다.")
    public ResponseEntity<Message> addDirectionInfo(@RequestParam Long golfFieldId,
                                                 @Valid @RequestBody GolfFieldRequest.DirectionsInfo request) {
        golfFieldService.addDirectionInfo(golfFieldId, request);
        return ResponseEntity.ok().body(new Message("골프장 오는길이 등록되었습니다."));
    }


    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "facility-info",consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "골프장 시설정보 입력 API", description="골프장 관리자 or 전체 시스템 관리자는 골프장 시설 안내를 입력한다.")
    public ResponseEntity<Message> addFacilityInfo(@RequestParam Long golfFieldId,
                                                @Valid @ModelAttribute FacilityRequest.Create request) {
        golfFieldService.addFacilityInfo(golfFieldId, request);
        return ResponseEntity.ok().body(new Message("골프장 시설 정보가 등록되었습니다."));
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("detail-info")
    @Operation(summary = "골프장 상세정보 조회 API", description="골프장 관리자 or 전체 시스템 관리자는 골프장 상세정보를 조회한다.")
    public ResponseEntity<GolfFieldResponse.Info> getInfo(@RequestParam Long golfFieldId) {
        return ResponseEntity.ok().body(golfFieldService.getDetail(golfFieldId));
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("direction-info")
    @Operation(summary = "골프장 오는 길 조회 API", description="골프장 관리자 or 전체 시스템 관리자는 골프장 오시는 길 정보를 조회한다.")
    public ResponseEntity<GolfFieldResponse.DirectionInfo> getDirectionInfo(@RequestParam Long golfFieldId) {
        return ResponseEntity.ok().body(golfFieldService.getDirectionInfo(golfFieldId));
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("facility-info")
    @Operation(summary = "골프장 시설정보 조회 API", description="골프장 관리자 or 전체 시스템 관리자는 골프장 시설을 조회한다.")
    public ResponseEntity<FacilityResponse> getFacilityInfo(@RequestParam Long golfFieldId) {
        return ResponseEntity.ok().body(facilityService.getFacility(golfFieldId));
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "facility-info",consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "골프장 시설정보 수정 API", description="골프장 관리자 or 전체 시스템 관리자는 골프장 시설 안내를 입력한다.")
    public ResponseEntity<Message> updateFacilityInfo(@RequestParam Long golfFieldId,
                                                   @Valid @ModelAttribute FacilityRequest.Update request) {
        golfFieldService.updateFacilityInfo(golfFieldId, request);
        return ResponseEntity.ok().body(new Message("골프장 시설정보가 수정되었습니다."));
    }
}
