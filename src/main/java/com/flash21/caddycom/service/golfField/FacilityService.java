package com.flash21.caddycom.service.golfField;

import com.flash21.caddycom.entity.golfField.Facility;
import com.flash21.caddycom.entity.golfField.FacilityImage;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.repository.golfField.FacilityImageJdbcRepository;
import com.flash21.caddycom.repository.golfField.FacilityImageRepository;
import com.flash21.caddycom.repository.golfField.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepository facilityRepository;
    private final FacilityImageJdbcRepository facilityImageRepository;

    @Transactional
    public void saveFacilityAndImageUrls(GolfField golfField, List<String> imageUrls, String name, String content){
        Facility facility = Facility.builder()
                .golfField(golfField)
                .name(name)
                .content(content)
                .build();
        facilityRepository.save(facility);

        List<FacilityImage> facilityImages = imageUrls.stream()
                        .map(url -> new FacilityImage(url, facility))
                        .toList();
        facilityImageRepository.saveAll(facilityImages);
    }
}
