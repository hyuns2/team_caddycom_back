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

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Component
@Profile({"prod","test"})
@RequiredArgsConstructor
public class S3FileUploader implements FileUploader{

    private final AmazonS3Client amazonS3Client;
    private static final int MAX_RETRY = 3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public String upload(MultipartFile file) {
        int tryCount = MAX_RETRY;

        // 원본의 확장자만 추출하여 고유한 파일 이름 설정
        String filename = file.getOriginalFilename();
        String extension =
                filename != null ? filename.substring(filename.lastIndexOf("."))
                        : "";
        String uniqueFilename = UUID.randomUUID() + extension;

        // 파일의 InputStream을 가져와 업로드
        while (tryCount > 0) {
            try (InputStream inputStream = file.getInputStream()) {
                // S3에 업로드할 파일의 메타데이터를 설정
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(file.getSize());
                metadata.setContentType(file.getContentType());

                amazonS3Client.putObject(new PutObjectRequest(bucket, uniqueFilename, inputStream, metadata));

                return amazonS3Client.getUrl(bucket, uniqueFilename).toString();

            } catch (Exception e) {
                tryCount--;
                log.error("S3 파일 업로드 중 오류 발생. 재시도 횟수: {}", MAX_RETRY - tryCount);
            }
        }

        throw new RuntimeException("이미지 업로드에 실패했습니다.");
    }
}
