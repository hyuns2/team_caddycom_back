package com.flash21.caddycom.entity.golfField;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String content;

    @OneToMany(mappedBy = "facility", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<FacilityImage> facilityImages;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "golf_field_id")
    private GolfField golfField;

    @Builder
    public Facility(GolfField golfField, String name, String content, List<String> images){
        this.golfField = golfField;
        this.name = name;
        this.content = content;
        this.facilityImages = images.stream()
                .map(url -> new FacilityImage(url, this))
                .toList();
    }

}
