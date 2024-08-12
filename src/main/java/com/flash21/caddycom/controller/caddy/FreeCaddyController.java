package com.flash21.caddycom.controller.caddy;

import com.flash21.caddycom.dto.Message;
import com.flash21.caddycom.dto.caddy.FreeCaddyRequest;
import com.flash21.caddycom.service.caddy.FreeCaddyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "5. Free Caddy", description = "프리캐디 API")
@RequestMapping("/api/free-caddy")
public class FreeCaddyController {
    private final FreeCaddyService freeCaddyService;
    @Operation(summary = "프리 캐디 등록", description = "프리 캐디의 정보를 저장한다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> saveFreeCaddy(@Valid @ModelAttribute FreeCaddyRequest.Create request) {
        freeCaddyService.saveFreeCaddy(request);
        return ResponseEntity.ok().body(new Message("프리 캐디 등록 성공"));
    }
}
