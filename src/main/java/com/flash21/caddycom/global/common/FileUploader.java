package com.flash21.caddycom.global.common;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploader {
    String upload(MultipartFile file);
    void delete(String url);
}
