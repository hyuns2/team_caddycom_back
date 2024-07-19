package com.flash21.caddycom.dto.golfFieldDetail.hole;

import com.flash21.caddycom.dto.golfFieldDetail.tee.TeeDto;
import com.flash21.caddycom.dto.golfFieldDetail.comment.CommentDto;
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
        @NotNull
        private Integer par;
    }

    @Getter
    @AllArgsConstructor
    public static class updateHandicap {
        @NotNull
        private Long holeId;
        @NotNull
        private Integer handicap;
    }

    @Getter
    @AllArgsConstructor
    public static class createDetailInfo {
        @NotNull
        private Long holeId;
        @NotNull
        private Integer par;
        @NotNull
        private Integer handicap;
        private List<TeeDto.Info> teeData;
        private List<CommentDto.Info> tipInfoData;
        private List<Long> deleteTeeIds;
        private List<Long> deleteTipInfoIds;
    }
}
