package com.flash21.caddycom.entity.golfField;

import com.flash21.caddycom.entity.account.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class GolfField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String contact;
    private String address;
    private String registrationNumber;
    private String businessLicense;
    private String employmentLicense;
    private String imageUrl;
    @Enumerated(EnumType.STRING)
    private CaddyType caddyType;
    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    private String fax;
    private String area;
    private LocalDate openingDate;
    private String cartInfo;
    private String publicTransportGuide;
    private String carGuide;
    private String amenities;

    @OneToMany(mappedBy = "golfField", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Facility> facilities;

    /**
     * 골프장과 골프장 관리자는 일대일 관계지만, 하나의 테이블에 나타냄
     */
    private String password;
    private Role role;




    public void approve(){
        this.status = ApprovalStatus.APPROVED;
    }
    public void reject(){
        this.status = ApprovalStatus.REJECT;
    }
    public void encodePassword(String password){
        this.password = password;
    }

    public void addInfo(String fax, String area, LocalDate openingDate, String cartInfo, String amenities){
        this.fax = fax;
        this.area = area;
        this.openingDate = openingDate;
        this.cartInfo = cartInfo;
        this.amenities = amenities;
    }

}