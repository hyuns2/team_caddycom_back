package com.flash21.caddycom.controller.golfField;

import com.flash21.caddycom.dto.Message;
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

@Tag(name = "1-1. GolfField", description = "골프장 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/golf-field")
public class GolfFieldController {
    private final GolfFieldService golfFieldService;


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

}