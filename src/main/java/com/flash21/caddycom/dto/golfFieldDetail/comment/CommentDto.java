package com.flash21.caddycom.dto.golfFieldDetail.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class CommentDto {
    @Getter
    @AllArgsConstructor
    public static class Info {
        private Long id;
        private String title;
        private String content;
    }
}
