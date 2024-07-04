package com.flash21.caddycom.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Sample", description = "샘플 API")
@RestController
@RequestMapping("/api")
public class SampleController {
    @GetMapping
    @Operation(summary="확인용 샘플 API")
    public ResponseEntity<String> sample(){
        return ResponseEntity.ok().body("안녕하세요");
    }
}
