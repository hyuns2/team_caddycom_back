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

@Tag(name = "Sample", description = "샘플 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/golf-field")
public class GolfFieldController {
    private final GolfFieldService golfFieldService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary="골프장 등록 API")
    public ResponseEntity<?> registerGolfField(@ModelAttribute @Valid GolfFieldRequest request){
        golfFieldService.registerGolfField(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
