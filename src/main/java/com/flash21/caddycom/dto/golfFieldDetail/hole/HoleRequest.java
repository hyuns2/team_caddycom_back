package com.flash21.caddycom.dto.golfFieldDetail.hole;

import com.flash21.caddycom.dto.golfFieldDetail.comment.CommentRequest;
import com.flash21.caddycom.dto.golfFieldDetail.tee.TeeDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class HoleRequest {
    @Getter
    @AllArgsConstructor
    public static class UpdatePar {
        @NotNull
        private Long holeId;
        @NotNull
        private Integer par;
    }

    @Getter
    @AllArgsConstructor
    public static class UpdateHandicap {
        @NotNull
        private Long holeId;
        @NotNull
        private Integer handicap;
    }

    @Getter
    @AllArgsConstructor
    @Setter
    @NoArgsConstructor
    public static class CreateDetailInfo {
        @NotNull
        private Long holeId;
        @NotNull
        private Integer par;
        @NotNull
        private Integer handicap;
        private List<TeeDto.Info> teeData;
        private List<CommentRequest.Create> commentData;
        private List<Long> deleteTeeIds;
        private List<Long> deleteCommentIds;
    }
}
