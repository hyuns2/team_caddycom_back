package com.flash21.caddycom.global.common.fileUploader;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Component
@Profile({"prod","test"})
@RequiredArgsConstructor
public class S3FileUploader implements FileUploader{

    private final AmazonS3Client amazonS3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public String upload(MultipartFile file, String directory) {
        // 원본의 확장자만 추출하여 고유한 파일 이름 설정
        String filename = file.getOriginalFilename();
        String extension =
                filename != null ? filename.substring(filename.lastIndexOf("."))
                        : "";
        String uniqueFilename = directory + UUID.randomUUID() + extension;

        // 파일의 InputStream을 가져와 업로드
        try (InputStream inputStream = file.getInputStream()) {
            // S3에 업로드할 파일의 메타데이터를 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3Client.putObject(new PutObjectRequest(bucket, uniqueFilename, inputStream, metadata));

        } catch (IOException e) {
            log.error("파일 업로드에 실패하여 재시도합니다.");
            throw new RuntimeException();
        }

        return amazonS3Client.getUrl(bucket, uniqueFilename).toString();
    }

    @Override
    public void delete(String url) {
        //ToDo: 구현 필요
    }
}
