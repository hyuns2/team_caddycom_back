package com.flash21.caddycom.controller.caddy.houseCaddy;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "4. House Caddy", description = "하우스캐디 API")
@RequestMapping("/api/house-caddy")
public class HouseCaddyController {
}
