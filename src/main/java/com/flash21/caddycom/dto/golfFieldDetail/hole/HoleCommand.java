package com.flash21.caddycom.dto.golfFieldDetail.hole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class HoleCommand {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class CreateDetailInfo {
        private Long holeId;
        private Integer par;
        private Integer handicap;
        private String image;

        public static CreateDetailInfo from(HoleRequest.CreateDetailInfo request, String imageUrl) {
            return CreateDetailInfo.builder()
                    .holeId(request.getHoleId())
                    .par(request.getPar())
                    .handicap(request.getHandicap())
                    .image(imageUrl)
                    .build();
        }
    }
}
