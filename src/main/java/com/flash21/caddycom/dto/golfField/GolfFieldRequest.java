package com.flash21.caddycom.dto.golfField;

import com.flash21.caddycom.entity.account.Role;
import com.flash21.caddycom.entity.golfField.ApprovalStatus;
import com.flash21.caddycom.entity.golfField.CaddyType;
import com.flash21.caddycom.entity.golfField.GolfField;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class GolfFieldRequest {
    @NotBlank(message = "name은 필수값입니다.")
    private String name;
    @NotBlank(message = "contact은 필수값입니다.")
    private String contact;
    @NotBlank(message = "address은 필수값입니다.")
    private String address;
    @NotBlank(message = "registerationNumber은 필수값입니다.")
    private String registrationNumber;

    @NotBlank(message = "caddyType은 필수값입니다.")
    private CaddyType caddyType;
    @NotBlank(message = "password은 필수값입니다.")
    private String password;

    private MultipartFile image;
    private MultipartFile businessLicense;
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
                //TODO: password 인코딩 필요
                .password(password)
                .role(Role.ROLE_MANAGER)
                .build();
    }


}
