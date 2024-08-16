package com.flash21.caddycom.entity.golfField;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String content;

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacilityImage> facilityImages;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "golf_field_id")
    private GolfField golfField;

    @Builder
    public Facility(GolfField golfField, String name, String content) {
        this.golfField = golfField;
        this.name = name;
        this.content = content;

        /** FacilityImage에서 fk를 가지고 저장하도록 수정됨.
         this.facilityImages = images.stream()
         .map(url -> new FacilityImage(url, this))
         .toList(); */
    }

    public void update(String name, String content) {
        this.name = name;
        this.content = content;
    }


    public static Facility create(GolfField golfField, String name, String content) {
        return Facility.builder()
                .golfField(golfField)
                .name(name)
                .content(content)
                .build();
    }
}
