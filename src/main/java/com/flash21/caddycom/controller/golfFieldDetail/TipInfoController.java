package com.flash21.caddycom.controller.golfFieldDetail;

import com.flash21.caddycom.dto.golfFieldDetail.tipInfo.TipInfoDto;
import com.flash21.caddycom.service.golfFieldDetail.TipInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name="2-5. TipInfo", description = "팁 정보 관련 API")
@RestController
@RequiredArgsConstructor
public class TipInfoController {
    private final TipInfoService tipInfoService;

    @GetMapping("/api/hole/{holeId}/tips")
    @Operation(summary = "홀의 전체 팁 정보 조회 API")
    public ResponseEntity<List<TipInfoDto.Info>> getAllTipInfos(@PathVariable Long holeId) {
        return new ResponseEntity<>(tipInfoService.getAllTipInfos(holeId), HttpStatus.OK);
    }
}
