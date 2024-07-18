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

@Tag(name = "1-2. GolfField Overview", description = "골프장 개요/시설 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/golf-field")
public class FacilityController {
    private final GolfFieldService golfFieldService;
    private final FacilityService facilityService;


    /**
     * 골프장 상세정보 C,R
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("detail-info")
    @Operation(summary = "골프장 상세정보 입력 API", description="골프장 관리자 or 전체 시스템 관리자는 골프장 추가정보를 입력, 수정한다.")
    public ResponseEntity<Message> addInfo(@RequestParam Long golfFieldId,
                                           @Valid @RequestBody GolfFieldRequest.AdditionalInfo request) {
        golfFieldService.createDetailInfo(golfFieldId, request);
        return ResponseEntity.ok().body(new Message("골프장 추가정보가 등록되었습니다."));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("detail-info")
    @Operation(summary = "골프장 상세정보 조회 API", description="골프장 관리자 or 전체 시스템 관리자는 골프장 상세정보를 조회한다.")
    public ResponseEntity<GolfFieldResponse.Info> getInfo(@RequestParam Long golfFieldId) {
        return ResponseEntity.ok().body(golfFieldService.getDetailInfo(golfFieldId));
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("detail-info")
    @Operation(summary = "골프장 상세정보 수정 API", description="골프장 관리자 or 전체 시스템 관리자는 골프장 정보를 수정한다.")
    public ResponseEntity<Message> update(@RequestParam Long golfFieldId,
                                          @Valid @RequestBody GolfFieldRequest.Update request) {
        golfFieldService.updateGolfField(golfFieldId, request);
        return ResponseEntity.ok().body(new Message("골프장이 수정되었습니다."));
    }



    /**
     * 골프장 오시는 길 C,R
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("direction-info")
    @Operation(summary = "골프장 오시는 길 입력 및 수정 API", description="골프장 관리자 or 전체 시스템 관리자는 골프장 오는 길 안내를 입력, 수정한다.")
    public ResponseEntity<Message> addDirectionInfo(@RequestParam Long golfFieldId,
                                                    @Valid @RequestBody GolfFieldRequest.DirectionsInfo request) {
        golfFieldService.createDirectionInfo(golfFieldId, request);
        return ResponseEntity.ok().body(new Message("골프장 오는길이 등록되었습니다."));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("direction-info")
    @Operation(summary = "골프장 오시는 길 조회 API", description="골프장 관리자 or 전체 시스템 관리자는 골프장 오시는 길 정보를 조회한다.")
    public ResponseEntity<GolfFieldResponse.DirectionInfo> getDirectionInfo(@RequestParam Long golfFieldId) {
        return ResponseEntity.ok().body(golfFieldService.getDirectionInfo(golfFieldId));
    }



    /**
     * 골프장 시설정보 C,R,U,D
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "facility-info",consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "골프장 시설정보 입력 API", description="골프장 관리자 or 전체 시스템 관리자는 골프장 시설 안내를 입력한다.")
    public ResponseEntity<Message> createFacility(@RequestParam Long golfFieldId,
                                                   @Valid @ModelAttribute FacilityRequest.Create request) {
        golfFieldService.createFacility(golfFieldId, request);
        return ResponseEntity.ok().body(new Message("골프장 시설 정보가 등록되었습니다."));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("facility-info")
    @Operation(summary = "골프장 시설정보 조회 API", description="골프장 관리자 or 전체 시스템 관리자는 골프장 시설을 조회한다.")
    public ResponseEntity<FacilityResponse> getFacility(@RequestParam Long golfFieldId) {
        return ResponseEntity.ok().body(facilityService.getFacility(golfFieldId));
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "facility-info",consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "골프장 시설정보 수정 API", description="골프장 관리자 or 전체 시스템 관리자는 골프장 시설 안내를 입력한다.")
    public ResponseEntity<Message> updateFacility(@RequestParam Long golfFieldId,
                                                      @Valid @ModelAttribute FacilityRequest.Update request) {
        golfFieldService.updateFacility(golfFieldId, request);
        return ResponseEntity.ok().body(new Message("골프장 시설정보가 수정되었습니다."));
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("facility-info")
    @Operation(summary = "골프장 시설정보 삭제 API", description="골프장 관리자 or 전체 시스템 관리자는 골프장 시설을 삭제한다.")
    public ResponseEntity<Message> deleteFacility(@RequestParam Long facilityId) {
        facilityService.deleteFacility(facilityId);
        return ResponseEntity.ok().body(new Message("골프장 시설정보가 삭제되었습니다."));
    }
}
