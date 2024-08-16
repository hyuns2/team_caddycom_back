package com.flash21.caddycom.service.golfField;

import com.flash21.caddycom.dto.golfField.FacilityResponse;
import com.flash21.caddycom.entity.golfField.Facility;
import com.flash21.caddycom.entity.golfField.FacilityImage;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.repository.golfField.FacilityImageJdbcRepositoryImpl;
import com.flash21.caddycom.repository.golfField.FacilityImageRepository;
import com.flash21.caddycom.repository.golfField.FacilityRepository;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * 골프장 시설 정보와 관련된 CRUD
 *
 * @author kwonssshyeon
 * @see FacilityRepository : 골프장 시설 정보 조회, 저장을 위한 repository
 * @see FacilityImageJdbcRepositoryImpl : 골프장 시설 이미지의 배치 insert를 위한 repository
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FacilityService {
    private final GolfFieldRepository golfFieldRepository;
    private final FacilityRepository facilityRepository;
    private final FacilityImageRepository facilityImageRepository;


    /**
     * 골프장 시설 정보를 저장한다.
     *
     * @param golfField 시설 정보를 추가할 골프장, null이 될 수 없다.
     * @param imageUrls 시설 이미지 url 리스트
     * @param name      시설 이름
     * @param content   시설 설명
     */
    @Transactional
    public void createFacilityAndFacilityImages(GolfField golfField, List<String> imageUrls, String name, String content) {
        Facility facility = Facility.create(golfField, name, content);
        facilityRepository.save(facility);

        List<FacilityImage> facilityImages = imageUrls.stream()
                .map(imageUrl -> new FacilityImage(imageUrl, facility))
                .toList();

        facilityImageRepository.bulkInsert(facilityImages);
    }


    /**
     * 골프장 시설 정보를 조회한다.
     *
     * @param id 조회할 골프장 id, null이 될 수 없다.
     * @return FacilityResponse 시설 정보 DTO
     * @throws NoSuchElementException 해당 골프장이 존재하지 않는 경우
     */
    public FacilityResponse getFacility(Long id) {
        GolfField golfField = golfFieldRepository.getGolfFieldById(id);
        return FacilityResponse.from(golfField.getFacilities());
    }

    /**
     * 골프장 시설 정보를 수정한다.
     * existingImageUrls에 없는 이미지는 삭제하고, newImageUrls은 새롭게 추가한다.
     *
     * @param id                수정할 시설 id, null이 될 수 없다.
     * @param name              시설 이름
     * @param content           시설 설명
     * @param existingImageUrls 기존 이미지 url 리스트(없어진 이미지를 삭제)
     * @param newImageUrls      새로 추가할 이미지 url 리스트
     * @throws NoSuchElementException 해당 시설이 존재하지 않는 경우
     */
    @Transactional
    public void updateFacilityInfo(Long id, String name, String content, List<Long> existingImageUrls, List<String> newImageUrls) {

        Facility facility = facilityRepository.findById(id).
                orElseThrow(() -> new NoSuchElementException("해당 시설은 존재하지 않습니다."));

        facility.getFacilityImages()
                .removeIf(facilityImage -> !existingImageUrls.contains(facilityImage.getId()));

        List<FacilityImage> facilityImages = newImageUrls.stream()
                .map(imageUrl -> new FacilityImage(imageUrl, facility))
                .toList();

        facilityImageRepository.bulkInsert(facilityImages);

        facility.update(name, content);
    }

    /**
     * 골프장 시설 정보를 삭제한다.
     *
     * @param id 삭제할 시설 id, null이 될 수 없다.
     */
    @Transactional
    public void deleteFacility(Long id) {

        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 시설은 존재하지 않습니다."));

        facilityRepository.delete(facility);
    }
}
