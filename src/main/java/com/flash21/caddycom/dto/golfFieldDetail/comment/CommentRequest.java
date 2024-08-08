package com.flash21.caddycom.dto.golfFieldDetail.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

public class CommentRequest {
    @Getter
    @AllArgsConstructor
    @Setter
    @NoArgsConstructor
    public static class Create {
        private Long id;
        private String title;
        private String content;
        private MultipartFile image;
    }
}
