package com.flash21.caddycom.dto.golfFieldDetail.comment;

import com.flash21.caddycom.entity.golfFieldDetail.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class CommentResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Info {
        private Long id;
        private String title;
        private String content;
        private String imageUrl;

        public static CommentResponse.Info of(Comment comment) {
            return Info.builder()
                    .id(comment.getId())
                    .title(comment.getTitle())
                    .content(comment.getContent())
                    .imageUrl(comment.getImageUrl())
                    .build();
        }

    }
}
