package com.flash21.caddycom.global.common;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Component
@Profile("local")
public class LocalFileUploader implements FileUploader{
    @Value("${local.storage.path:C:/}")
    private String uploadPath;
    @Override
    public String upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        UUID uuid = UUID.randomUUID();
        String savedFilename = uploadPath + uuid.toString() + "_" + originalFilename;
        File file1 = new File(savedFilename);
        try {
            file.transferTo(file1);
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드에 실패했습니다.");
        }

        return savedFilename;
    }
    @Override
    public void delete(String url) {
         File file = new File(url);
         file.delete();
    }
}
