package com.flash21.caddycom.dto.golfFieldDetail.hole;

import com.flash21.caddycom.dto.golfFieldDetail.tee.TeeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class HoleResponse {
    @Getter
    @AllArgsConstructor
    public static class info {
        private Long id;
        private Integer num;
        private Integer par;
        private Integer handicap;
        private List<TeeDto.info> teeInfos;
    }
}
