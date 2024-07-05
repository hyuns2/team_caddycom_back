package com.flash21.caddycom.dto;

import com.flash21.caddycom.entity.Formation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CourseInfoResponseDto {
    @Schema(description = "코스 id")
    Long id;

    @Schema(description = "코스 이름")
    String name;

    @Schema(description = "코스의 홀 총 개수")
    int totalHoles;
}
