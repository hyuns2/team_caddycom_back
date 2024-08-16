package com.flash21.caddycom.dto.golfFieldDetail.hole;

import com.flash21.caddycom.dto.golfFieldDetail.comment.CommentRequest;
import com.flash21.caddycom.dto.golfFieldDetail.tee.TeeResponse;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class HoleRequest {
    @Getter
    @AllArgsConstructor
    public static class UpdatePar {
        @NotNull(message = "holeId는 필수값입니다.")
        private Long holeId;

        @NotNull(message = "par는 필수값입니다.")
        private Integer par;
    }

    @Getter
    @AllArgsConstructor
    public static class UpdateHandicap {
        @NotNull(message = "holeId는 필수값입니다.")
        private Long holeId;
        @NotNull(message = "handicap은 필수값입니다.")
        private Integer handicap;
    }

    @Getter
    @AllArgsConstructor
    @Setter
    @NoArgsConstructor
    public static class CreateDetailInfo {
        @NotNull(message = "holeId는 필수값입니다.")
        private Long holeId;
        @NotNull(message = "par는 필수값입니다.")
        private Integer par;
        @NotNull(message = "handicap은 필수값입니다.")
        private Integer handicap;
        private MultipartFile image;
        private List<TeeResponse.Info> teeData;
        private List<CommentRequest.Create> commentData;
        private List<Long> deleteTeeIds;
        private List<Long> deleteCommentIds;
    }
}
