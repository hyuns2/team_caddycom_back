package com.flash21.caddycom.service.golfField;

import com.amazonaws.util.CollectionUtils;
import com.flash21.caddycom.dto.golfField.FacilityRequest;
import com.flash21.caddycom.dto.golfField.GolfFieldRequest;
import com.flash21.caddycom.dto.golfField.GolfFieldResponse;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.global.common.fileUploader.FileUploader;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import com.flash21.caddycom.repository.golfStaff.GolfStaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


/**
 * 골프장 정보와 관련된 CRUD
 */
@Service
@RequiredArgsConstructor
public class GolfFieldService {
    private final FileUploader fileUploader;
    private final GolfFieldRepository golfFieldRepository;
    private final FacilityService facilityService;
    private final GolfStaffRepository golfStaffRepository;
    private final PlatformTransactionManager transactionManager;

    /**
     * 골프장을 생성
     */
    public void createGolfField(String phoneNumber, GolfFieldRequest.Create request) {
        List<String> fileUrls = uploadFiles(List.of(request.getImage(),
                                                    request.getBusinessLicense(),
                                                    request.getEmploymentLicense()));

        GolfField golfField = request.toEntity(fileUrls.get(0), fileUrls.get(1), fileUrls.get(2));

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(status -> {
            saveFieldAndSetStaff(golfField, phoneNumber);
            return null;
        });
    }


    protected void saveFieldAndSetStaff(GolfField golfField, String phoneNumber) {
        golfFieldRepository.save(golfField);
        golfStaffRepository.findByPhoneNumber(phoneNumber)
                .ifPresent(staff -> staff.linkGolfField(golfField));
    }


    /**
     * 골프장 전체 조회
     */
    @Transactional(readOnly = true)
    public List<GolfFieldResponse.Overview> getAll() {
        return golfFieldRepository.findAll().stream()
                .map(GolfFieldResponse.Overview::from)
                .collect(Collectors.toList());
    }

    /**
     * 골프장 수정
     */
    @Transactional
    public void updateGolfField(Long id, GolfFieldRequest.Update request) {
        GolfField golfField = golfFieldRepository.getGolfFieldById(id);

        golfField.update(request.getName(),
                         request.getAddress(),
                         request.getAddressDetail(),
                         request.getContact(),
                         request.getFax(),
                         request.getArea(),
                         request.getOpeningDate(),
                         request.getCartInfo(),
                         request.getAmenities());
    }

    /**
     * 골프장 삭제
     */
    @Transactional
    public void deleteGolfField(Long id) {
        GolfField golfField = golfFieldRepository.getGolfFieldById(id);
        golfFieldRepository.delete(golfField);
    }


    /**
     * 골프장 상세정보 입력
     */
    @Transactional
    public void createDetailInfo(Long id, GolfFieldRequest.AdditionalInfo request) {
        GolfField golfField = golfFieldRepository.getGolfFieldById(id);

        golfField.addInfo(request.getFax(),
                          request.getArea(),
                          request.getOpeningDate(),
                          request.getCartInfo(),
                          request.getAmenities());
    }

    /**
     * 골프장 상세 조회
     */
    @Transactional(readOnly = true)
    public GolfFieldResponse.Info getDetailInfo(Long id) {
        GolfField golfField = golfFieldRepository.getGolfFieldById(id);
        return GolfFieldResponse.Info.from(golfField);
    }


    /**
     * 골프장 오는길 정보 입력
     */
    @Transactional
    public void createDirectionInfo(Long id, GolfFieldRequest.DirectionsInfo request) {
        GolfField golfField = golfFieldRepository.getGolfFieldById(id);
        golfField.addDirectionInfo(request.getPublicTransportGuide(), request.getCarGuide());
    }

    /**
     * 골프장 오는길 정보 조회
     */
    @Transactional(readOnly = true)
    public GolfFieldResponse.DirectionInfo getDirectionInfo(Long id) {
        GolfField golfField = golfFieldRepository.getGolfFieldById(id);
        return GolfFieldResponse.DirectionInfo.from(golfField);
    }


    /**
     * 골프장 시설 정보 입력
     */
    public void createFacility(Long id, FacilityRequest.Create request) {
        GolfField golfField = golfFieldRepository.getGolfFieldById(id);

        List<String> facilityImages = (CollectionUtils.isNullOrEmpty(request.getFacilityImages()))
                ? Collections.emptyList()
                : uploadFiles(request.getFacilityImages());

        facilityService.createFacilityAndFacilityImages(golfField, facilityImages,
                request.getName(),
                request.getContent());
    }

    /***
     * 골프장 시설 정보 수정
     */
    public void updateFacility(Long id, FacilityRequest.Update request) {
        golfFieldRepository.getGolfFieldById(id);

        List<String> facilityImages = (CollectionUtils.isNullOrEmpty(request.getFacilityImages()))
                ? Collections.emptyList()
                : uploadFiles(request.getFacilityImages());

        facilityService.updateFacilityInfo(request.getFacilityId(),
                                           request.getName(),
                                           request.getContent(),
                                           request.getExistingImageIds(),
                                           facilityImages);
    }


    /**
     * 외부 저장소에 파일 업로드
     * 트랜젝션 시작 전 수행된다.
     */
    private List<String> uploadFiles(List<MultipartFile> files) {
        return files.stream()
                .map(file -> fileUploader.upload(file, "credentials/"))
                .collect(Collectors.toList());
    }
}
