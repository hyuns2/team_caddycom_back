package com.flash21.caddycom.dto.golfFieldDetail.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

public class CommentRequest {
    @Getter
    @AllArgsConstructor
    public static class Create {
        private Long id;
        private String title;
        private String content;
        private MultipartFile image;
    }
}
