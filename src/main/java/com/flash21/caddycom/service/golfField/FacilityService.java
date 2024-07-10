package com.flash21.caddycom.service.golfField;

import com.flash21.caddycom.dto.golfField.FacilityResponse;
import com.flash21.caddycom.entity.golfField.Facility;
import com.flash21.caddycom.entity.golfField.FacilityImage;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.repository.golfField.FacilityImageJdbcRepository;
import com.flash21.caddycom.repository.golfField.FacilityImageRepository;
import com.flash21.caddycom.repository.golfField.FacilityRepository;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 골프장 시설 정보와 관련된 CRUD
 *
 * @see FacilityRepository : 골프장 시설 정보 조회, 저장을 위한 repository
 * @see FacilityImageJdbcRepository : 골프장 시설 이미지의 배치 insert를 위한 repository
 * @author kwonssshyeon
 */
@Service
@RequiredArgsConstructor
public class FacilityService {
    private final GolfFieldRepository golfFieldRepository;
    private final FacilityRepository facilityRepository;
    private final FacilityImageJdbcRepository facilityImageRepository;


    /**
     * 골프장 시설 정보를 저장한다.
     * @param golfField 시설 정보를 추가할 골프장, null이 될 수 없다.
     * @param imageUrls 시설 이미지 url 리스트
     * @param name 시설 이름
     * @param content  시설 설명
     */
    @Transactional
    public void createFacilityAndFacilityImages(GolfField golfField, List<String> imageUrls, String name, String content){
        Facility facility = Facility.builder()
                .golfField(golfField)
                .name(name)
                .content(content)
                .build();
        facilityRepository.save(facility);

        List<FacilityImage> facilityImages = imageUrls.stream()
                        .map(imageUrl -> new FacilityImage(imageUrl, facility))
                        .toList();
        facilityImageRepository.saveAll(facilityImages);
    }


    /**
     * 골프장 시설 정보를 조회한다.
     * @param id 조회할 골프장 id, null이 될 수 없다.
     * @return FacilityResponse 시설 정보 DTO
     * @throws NoSuchElementException 해당 골프장이 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public FacilityResponse getFacility(Long id) {
        GolfField golfField = golfFieldRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 골프장은 존재하지 않습니다."));

        return FacilityResponse.from(golfField.getFacilities());
    }
}
