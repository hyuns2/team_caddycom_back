package com.flash21.caddycom.dto.golfField;

import com.flash21.caddycom.entity.account.Role;
import com.flash21.caddycom.entity.golfField.ApprovalStatus;
import com.flash21.caddycom.entity.golfField.CaddyType;
import com.flash21.caddycom.entity.golfField.Facility;
import com.flash21.caddycom.entity.golfField.GolfField;
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
        private String name;
        @NotBlank(message = "contact은 필수값입니다.")
        private String contact;
        @NotBlank(message = "address은 필수값입니다.")
        private String address;
        @NotBlank(message = "addressDetail은 필수값입니다.")
        private String addressDetail;
        @NotBlank(message = "registerationNumber은 필수값입니다.")
        private String registrationNumber;

        @NotNull(message = "caddyType은 필수값입니다.")
        private CaddyType caddyType;
        @NotBlank(message = "password은 필수값입니다.")
        private String password;

        @NotNull(message = "image은 필수값입니다.")
        private MultipartFile image;
        @NotNull(message = "businessLicense은 필수값입니다.")
        private MultipartFile businessLicense;
        @NotNull(message = "employmentLicense은 필수값입니다.")
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
        private String fax;
        private String area;
        private LocalDate openingDate;
        private String cartInfo;
        private String amenities;
    }

    @Getter
    @AllArgsConstructor
    public static class FacilityInfo {
        @NotBlank(message = "name은 필수값입니다.")
        private String name;
        private String content;
        private List<MultipartFile> facilityImages;
    }




}
