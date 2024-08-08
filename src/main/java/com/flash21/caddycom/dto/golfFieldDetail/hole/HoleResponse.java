package com.flash21.caddycom.dto.golfFieldDetail.hole;

import com.flash21.caddycom.entity.golfFieldDetail.Comment;
import com.flash21.caddycom.entity.golfFieldDetail.Hole;
import com.flash21.caddycom.entity.golfFieldDetail.Tee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class HoleResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class HoleInfo {
        private Long id;
        private Integer num;
        private Integer par;
        private Integer handicap;
        private String imageUrl;
        private List<TeeInfo> teeInfos;
        private List<CommentInfo> commentInfos;

        public static HoleInfo from(Hole hole) {
            return HoleInfo.builder()
                    .id(hole.getId())
                    .num(hole.getNum())
                    .par(hole.getPar())
                    .handicap(hole.getHandicap())
                    .imageUrl(hole.getImageUrl())
                    .teeInfos(hole.getTees().stream()
                            .map(TeeInfo::from)
                            .toList())
                    .commentInfos(hole.getComments().stream()
                            .map(CommentInfo::from)
                            .toList())
                    .build();
        }

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TeeInfo {
            private Long id;
            private String name;
            private Integer distance;

            public static TeeInfo from(Tee tee) {
                return TeeInfo.builder()
                        .id(tee.getId())
                        .name(tee.getName())
                        .distance(tee.getDistance())
                        .build();
            }
        }


        @Getter
        @Builder
        @AllArgsConstructor
        public static class CommentInfo {
            private Long id;
            private String title;
            private String content;
            private String imageUrl;

            public static CommentInfo from(Comment comment) {
                return CommentInfo.builder()
                        .id(comment.getId())
                        .title(comment.getTitle())
                        .content(comment.getContent())
                        .imageUrl(comment.getImageUrl())
                        .build();
            }
        }
    }
}
