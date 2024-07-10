package com.flash21.caddycom.dto.golfFieldDetail.hole;

import com.flash21.caddycom.dto.golfFieldDetail.tee.TeeDto;
import com.flash21.caddycom.dto.golfFieldDetail.tipInfo.TipInfoDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class HoleRequest {
    @Getter
    @AllArgsConstructor
    public static class updatePar {
        @NotNull
        private Long holeId;

        private int par;
    }

    @Getter
    @AllArgsConstructor
    public static class updateHandicap {
        private Long holeId;
        private int handicap;
    }

    @Getter
    @AllArgsConstructor
    public static class createDetailInfo {
        private Long holeId;
        private int par;
        private int handicap;
        private List<TeeDto.info> teeData;
        private List<TipInfoDto.info> tipInfoData;
        private List<Long> deleteTeeIds;
        private List<Long> deleteTipInfoIds;
    }
}
