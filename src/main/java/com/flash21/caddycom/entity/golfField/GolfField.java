package com.flash21.caddycom.entity.golfField;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class GolfField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String contact;
    private String address;
    private String registerationNumber;
    private String businessLicense;
    private String employmentLicense;
    private String imageUrl;
    @Enumerated(EnumType.STRING)
    private CaddyType caddyType;
    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;
    private String password;

    public void approve(){
        this.status = ApprovalStatus.COMPLETE;
    }
    public void reject(){
        this.status = ApprovalStatus.REJECT;
    }
}