package com.flash21.caddycom.entity.golfField;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacilityImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "facility_id")
    private Facility facility;

    public FacilityImage(String imageUrl, Facility facility) {
        this.imageUrl = imageUrl;
        this.facility = facility;
    }


}
