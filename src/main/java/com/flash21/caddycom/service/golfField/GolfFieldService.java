package com.flash21.caddycom.service.golfField;

import com.flash21.caddycom.dto.golfField.GolfFieldRequest;
import com.flash21.caddycom.dto.golfField.GolfFieldResponse;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.repository.GolfFieldRepository;
import com.flash21.caddycom.service.S3FileUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class GolfFieldService {
    private final S3FileUploader s3FileUploader;
    private final GolfFieldRepository golfFieldRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerGolfField(GolfFieldRequest request){
        String imageUrl = s3FileUploader.upload(request.getImage());
        String businessLicense = s3FileUploader.upload(request.getBusinessLicense());
        String employmentLicense = s3FileUploader.upload(request.getEmploymentLicense());

        GolfField golfField = request.toEntity(imageUrl,businessLicense,employmentLicense);
        golfField.encodePassword(passwordEncoder.encode(request.getPassword()));
        golfFieldRepository.save(golfField);
    }

    @Transactional(readOnly = true)
    public List<GolfFieldResponse.Overview> getAll(){
        return golfFieldRepository.findAll().stream()
                .map(golfField -> new GolfFieldResponse.Overview(golfField.getName(),golfField.getContact()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteGolfField(Long id){
        GolfField golfField = golfFieldRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 골프장은 존재하지 않습니다."));
        golfFieldRepository.delete(golfField);
    }

    @Transactional
    public void updateGolfField(Long id){
        GolfField golfField = golfFieldRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 골프장은 존재하지 않습니다."));
        golfFieldRepository.delete(golfField);
    }
}
