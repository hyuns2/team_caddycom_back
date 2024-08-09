package com.flash21.caddycom.service.golfField;

import com.amazonaws.util.CollectionUtils;
import com.flash21.caddycom.dto.golfField.FacilityRequest;
import com.flash21.caddycom.dto.golfField.GolfFieldRequest;
import com.flash21.caddycom.dto.golfField.GolfFieldResponse;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.global.common.fileUploader.FileUploader;
import com.flash21.caddycom.repository.account.AccountRepository;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


/**
 * 골프장 정보와 관련된 CRUD
 *
 * @author kwonssshyeon
 * @see FileUploader : 파일 업로드를 위한 class
 * @see GolfFieldRepository : 골프장 조회, 저장을 위한 repository
 * @see FacilityService : 골프장 시설 정보를 위한 class
 * @see PasswordEncoder : 비밀번호 암호화를 위한 class
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GolfFieldService {
    private final FileUploader fileUploader;
    private final GolfFieldRepository golfFieldRepository;
    private final FacilityService facilityService;
    private final AccountRepository accountRepository;

    /**
     * 골프장을 생성
     *
     * @param request 골프장 생성 요청 DTO
     */
    @Transactional
    public void createGolfField(GolfFieldRequest.Create request) {
        List<String> fileUrls = uploadFiles(List.of(request.getImage(),
                request.getBusinessLicense(),
                request.getEmploymentLicense()));

        GolfField golfField = request.toEntity(fileUrls.get(0), fileUrls.get(1), fileUrls.get(2));
        golfFieldRepository.save(golfField);
        accountRepository.findByPhoneNumber(request.getContact())
                .ifPresent(account -> account.linkGolfField(golfField));
    }

    /**
     * 골프장 전체 조회
     *
     * @return 골프장 전체 조회 응답 DTO 리스트
     */
    public List<GolfFieldResponse.Overview> getAll() {
        return golfFieldRepository.findAll().stream()
                .map(GolfFieldResponse.Overview::from)
                .collect(Collectors.toList());
    }

    /**
     * 골프장 수정
     *
     * @param id      수정할 골프장 id, null이 들어갈 수 없다.
     * @param request 수정할 골프장 정보 DTO
     */
    @Transactional
    public void updateGolfField(Long id, GolfFieldRequest.Update request) {
        GolfField golfField = golfFieldRepository.getUserById(id);

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
     *
     * @param id 삭제할 골프장 id, null이 들어갈 수 없다.
     * @throws NoSuchElementException 해당 골프장이 존재하지 않는 경우
     */
    @Transactional
    public void deleteGolfField(Long id) {
        GolfField golfField = golfFieldRepository.getUserById(id);
        golfFieldRepository.delete(golfField);
    }


    /**
     * 골프장 상세정보 입력
     *
     * @param id      추가정보를 입력할 골프장 id, null이 들어갈 수 없다.
     * @param request 추가정보 입력 요청 DTO
     * @throws NoSuchElementException 해당 골프장이 존재하지 않는 경우
     */
    @Transactional
    public void createDetailInfo(Long id, GolfFieldRequest.AdditionalInfo request) {
        GolfField golfField = golfFieldRepository.getUserById(id);

        golfField.addInfo(request.getFax(),
                request.getArea(),
                request.getOpeningDate(),
                request.getCartInfo(),
                request.getAmenities());

        golfFieldRepository.save(golfField);
    }

    /**
     * 골프장 상세 조회
     *
     * @param id 상세정보를 조회할 골프장 id, null이 들어갈 수 없다.
     * @return GolfFieldResponse.Info 골프장 상세 조회 응답 DTO
     * @throws NoSuchElementException 해당 골프장이 존재하지 않는 경우
     */
    public GolfFieldResponse.Info getDetailInfo(Long id) {
        GolfField golfField = golfFieldRepository.getUserById(id);
        return GolfFieldResponse.Info.from(golfField);
    }


    /**
     * 골프장 오는길 정보 입력
     *
     * @param id      추가정보를 입력할 골프장 id, null이 들어갈 수 없다.
     * @param request 오는길 정보 입력 요청 DTO
     * @throws NoSuchElementException 해당 골프장이 존재하지 않는 경우
     */
    @Transactional
    public void createDirectionInfo(Long id, GolfFieldRequest.DirectionsInfo request) {
        GolfField golfField = golfFieldRepository.getUserById(id);
        golfField.addDirectionInfo(request.getPublicTransportGuide(), request.getCarGuide());

        golfFieldRepository.save(golfField);
    }

    /**
     * 골프장 오는길 정보 조회
     *
     * @param id 오는길 정보를 조회할 골프장 id, null이 들어갈 수 없다.
     * @return GolfFieldResponse.DirectionInfo 골프장 오는길 정보 조회 응답 DTO
     * @throws NoSuchElementException 해당 골프장이 존재하지 않는 경우
     */
    public GolfFieldResponse.DirectionInfo getDirectionInfo(Long id) {
        GolfField golfField = golfFieldRepository.getUserById(id);
        return GolfFieldResponse.DirectionInfo.from(golfField);
    }


    /**
     * 골프장 시설 정보 입력
     *
     * @param id      추가정보를 입력할 골프장 id, null이 들어갈 수 없다.
     * @param request 시설 정보 입력 요청 DTO
     * @throws NoSuchElementException 해당 골프장이 존재하지 않는 경우
     */
    @Transactional
    public void createFacility(Long id, FacilityRequest.Create request) {
        GolfField golfField = golfFieldRepository.getUserById(id);

        List<String> facilityImages = (CollectionUtils.isNullOrEmpty(request.getFacilityImages()))
                ? Collections.emptyList()
                : uploadFiles(request.getFacilityImages());

        facilityService.createFacilityAndFacilityImages(golfField, facilityImages,
                request.getName(),
                request.getContent());
    }

    /***
     * 골프장 시설 정보 수정
     * @param id 추가정보를 입력할 골프장 id, null이 들어갈 수 없다.
     * @param request 시설 정보 입력 요청 DTO
     * @throws NoSuchElementException 해당 골프장이 존재하지 않는 경우
     */
    @Transactional
    public void updateFacility(Long id, FacilityRequest.Update request) {
        golfFieldRepository.getUserById(id);

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
     *
     * @param files 업로드할 파일 리스트
     * @return 업로드된 파일 url 리스트
     */
    private List<String> uploadFiles(List<MultipartFile> files) {
        return files.stream()
                .map(fileUploader::upload)
                .collect(Collectors.toList());
    }
}
