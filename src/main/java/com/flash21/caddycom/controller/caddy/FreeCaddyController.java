package com.flash21.caddycom.controller.caddy;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "5. Free Caddy", description = "프리캐디 API")
@RequestMapping("/api/free-caddy")
public class FreeCaddyController {
}
