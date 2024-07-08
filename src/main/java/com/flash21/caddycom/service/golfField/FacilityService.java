package com.flash21.caddycom.service.golfField;

import com.flash21.caddycom.entity.golfField.Facility;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepository facilityRepository;

    @Transactional
    public void saveFacilityAndImageUrls(GolfField golfField, List<String> facilityImages, String name, String content){
        Facility facility = Facility.builder()
                .golfField(golfField)
                .name(name)
                .content(content)
                .images(facilityImages)
                .build();
        facilityRepository.save(facility);
    }
}
