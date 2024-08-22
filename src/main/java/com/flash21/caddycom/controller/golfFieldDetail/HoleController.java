package com.flash21.caddycom.controller.golfFieldDetail;

import com.flash21.caddycom.dto.golfFieldDetail.hole.HoleRequest;
import com.flash21.caddycom.dto.golfFieldDetail.hole.HoleResponse;
import com.flash21.caddycom.service.golfFieldDetail.HoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "2-2. Course Detail", description = "골프장 코스 상세 정보(홀,티) 관련 API")
@RestController
@RequiredArgsConstructor
public class HoleController {
    private final HoleService holeService;

    @PatchMapping("/api/hole/handicap")
    @Operation(summary = "홀의 핸디 수정 API", description = "홀의 핸디(handicap) 값을 수정한다.")
    public void updateHandicap(@Valid  @RequestBody HoleRequest.UpdateHandicap request) {
        holeService.updateHandicap(request);
    }

    @PatchMapping("/api/hole/par")
    @Operation(summary = "홀의 파 수정 API", description = "홀의 파(par) 값을 수정한다.")
    public void updatePar(@Valid @RequestBody HoleRequest.UpdatePar request) {
        holeService.updatePar(request);
    }

    @PostMapping(value="/api/hole/detail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "홀의 상세 정보 설정 API", description = """
            홀의 상세 정보를 설정한다. 해당 홀의 티와 멘트 정보도 포함된다. <br>
            **홀 이미지 업로드 및 티/멘트 생성/수정은 swagger를 통해 테스트 불가하니 유의**
            - 이미지 업로드: String to MultipartFile convert error
            - 티/멘트: String to List convert error
                - MULTIPART_FORM_DATA_VALUE로 받기 때문에 request를 아래와 같은 방식으로 보내야하나 json 같은 방식으로 보내기 때문으로 추측 중
                    - teeData[0].id = ?, teeData[0].name = ? teeData[0].distance = ?
                    - 인덱스 직접 지정 필요
                    - 이는 commentData도 동일
            """)
    public ResponseEntity<HoleResponse.HoleInfo> createDetailInfo(@Valid @ModelAttribute HoleRequest.CreateDetailInfo request) {
        return new ResponseEntity<>(holeService.processDetailInfo(request), HttpStatus.OK);
    }
}
