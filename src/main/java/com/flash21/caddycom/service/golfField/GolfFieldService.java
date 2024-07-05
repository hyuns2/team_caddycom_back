package com.flash21.caddycom.service.golfField;

import com.flash21.caddycom.dto.golfField.GolfFieldRequest;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.repository.GolfFieldRepository;
import com.flash21.caddycom.service.S3FileUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class GolfFieldService {
    private final S3FileUploader s3FileUploader;
    private final GolfFieldRepository golfFieldRepository;
    private final PasswordEncoder passwordEncoder;


    public void registerGolfField(GolfFieldRequest request){
        String imageUrl = s3FileUploader.upload(request.getImage());
        String businessLicense = s3FileUploader.upload(request.getBusinessLicense());
        String employmentLicense = s3FileUploader.upload(request.getEmploymentLicense());

        GolfField golfField = request.toEntity(imageUrl,businessLicense,employmentLicense);
        golfField.encodePassword(passwordEncoder.encode(request.getPassword()));
        golfFieldRepository.save(golfField);
    }
}
