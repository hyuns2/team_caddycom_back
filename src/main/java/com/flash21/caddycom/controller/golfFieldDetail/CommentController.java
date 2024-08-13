package com.flash21.caddycom.controller.golfFieldDetail;

import com.flash21.caddycom.dto.golfFieldDetail.comment.CommentResponse;
import com.flash21.caddycom.service.golfFieldDetail.CommentService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "2-2. GolfField Detail", description = "골프장 코스 상세 정보(홀,티) 관련 API")
@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @Hidden
    @GetMapping("/api/hole/{holeId}/tips")
    @Operation(summary = "홀의 전체 팁 정보 조회 API")
    public ResponseEntity<List<CommentResponse.Info>> getAllComments(@PathVariable Long holeId) {
        return new ResponseEntity<>(commentService.getAllComments(holeId), HttpStatus.OK);
    }
}
