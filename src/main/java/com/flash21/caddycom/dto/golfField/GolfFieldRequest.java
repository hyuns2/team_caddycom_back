package com.flash21.caddycom.dto.golfField;

import com.flash21.caddycom.entity.account.Role;
import com.flash21.caddycom.entity.golfField.ApprovalStatus;
import com.flash21.caddycom.entity.golfField.CaddyType;
import com.flash21.caddycom.entity.golfField.GolfField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;


public class GolfFieldRequest {
    @Getter
    @AllArgsConstructor
    public static class Create{
        @NotBlank(message = "name은 필수값입니다.")
        @Schema(example = "골프장 이름(String)")
        private String name;

        @NotBlank(message = "contact은 필수값입니다.")
        @Schema(example = "골프장 연락처(053-000-0000)")
        private String contact;

        @NotBlank(message = "address은 필수값입니다.")
        @Schema(example = "골프장 주소(String)")
        private String address;

        @NotBlank(message = "addressDetail은 필수값입니다.")
        @Schema(example = "골프장 상세주소(String)")
        private String addressDetail;

        @NotBlank(message = "registerationNumber은 필수값입니다.")
        @Schema(example = "사업자 등록 번호(String)")
        private String registrationNumber;

        @NotNull(message = "caddyType은 필수값입니다.")
        @Schema(example = "골프장 캐디 타입(String)")
        private CaddyType caddyType;

        @NotBlank(message = "password은 필수값입니다.")
        @Schema(example = "비밀번호 6자리(String)")
        private String password;

        @NotNull(message = "image은 필수값입니다.")
        @Schema(example = "골프장 대표사진(MultipartFile)")
        private MultipartFile image;

        @NotNull(message = "businessLicense은 필수값입니다.")
        @Schema(example = "사업자 등록증(MultipartFile)")
        private MultipartFile businessLicense;

        @NotNull(message = "employmentLicense은 필수값입니다.")
        @Schema(example = "재직 증명서(MultipartFile)")
        private MultipartFile employmentLicense;

        public GolfField toEntity(String imageUrl, String businessLicenseUrl, String employmentLicenseUrl){
            return GolfField.builder()
                    .name(name)
                    .contact(contact)
                    .address(address)
                    .registrationNumber(registrationNumber)
                    .businessLicense(businessLicenseUrl)
                    .employmentLicense(employmentLicenseUrl)
                    .imageUrl(imageUrl)
                    .caddyType(caddyType)
                    .status(ApprovalStatus.WAITING)
                    .password(password)
                    .role(Role.ROLE_MANAGER)
                    .build();
        }
    }


    @Getter
    @AllArgsConstructor
    public static class AdditionalInfo {
        @Schema(example = "골프장 팩스번호(String)")
        private String fax;

        @Schema(example = "골프장 면적(String)")
        private String area;

        @Schema(example = "골프장 개장일(2024-00-00)")
        private LocalDate openingDate;

        @Schema(example = "골프장 카트 정보(String)")
        private String cartInfo;

        @Schema(example = "골프장 부대시설(String)")
        private String amenities;
    }


    @Getter
    @AllArgsConstructor
    public static class DirectionsInfo {
        @Schema(example = "대중교통 안내(String)")
        private String publicTransportGuide;

        @Schema(example = "자가용 안내(String)")
        private String carGuide;
    }


    @Getter
    @AllArgsConstructor
    public static class FacilityInfo {
        @NotBlank(message = "name은 필수값입니다.")
        private String name;

        private String content;

        private List<MultipartFile> facilityImages;
    }


    @Getter
    @AllArgsConstructor
    public static class Update {
        @NotBlank(message = "name은 필수값입니다.")
        @Schema(example = "골프장 이름(String)")
        private String name;

        @NotBlank(message = "address은 필수값입니다.")
        @Schema(example = "골프장 주소(String)")
        private String address;

        @NotBlank(message = "addressDetail은 필수값입니다.")
        @Schema(example = "골프장 상세주소(String)")
        private String addressDetail;

        @NotBlank(message = "contact은 필수값입니다.")
        @Schema(example = "골프장 연락처(053-000-0000)")
        private String contact;

        @Schema(example = "골프장 팩스번호(String)")
        private String fax;

        @Schema(example = "골프장 면적(String)")
        private String area;

        @Schema(example = "골프장 개장일(2024-00-00)")
        private LocalDate openingDate;

        @Schema(example = "골프장 카트 정보(String)")
        private String cartInfo;

        @Schema(example = "골프장 부대시설(String)")
        private String amenities;
    }
}
