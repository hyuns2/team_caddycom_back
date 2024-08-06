package com.flash21.caddycom.global.common.fileUploader;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploader {
    String upload(MultipartFile file);
}
