package com.flash21.caddycom.dto.golfFieldDetail.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class CommentResponse {
    @Getter
    @AllArgsConstructor
    public static class Info {
        private Long id;
        private String title;
        private String content;
        private String imageUrl;
    }
}
