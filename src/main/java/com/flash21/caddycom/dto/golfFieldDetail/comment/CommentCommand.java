package com.flash21.caddycom.dto.golfFieldDetail.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class CommentCommand {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Create {
        private Long id;
        private String title;
        private String content;
        private String image;

        public static Create from(CommentRequest.Create request, String imageUrl) {
            return Create.builder()
                    .id(request.getId())
                    .title(request.getTitle())
                    .content(request.getContent())
                    .image(imageUrl)
                    .build();
        }
    }
}
