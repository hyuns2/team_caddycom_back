package com.flash21.caddycom.dto.golfField;

import com.flash21.caddycom.entity.golfField.Facility;
import com.flash21.caddycom.entity.golfField.FacilityImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
@AllArgsConstructor
public class FacilityResponse {
    private List<Item> facilities;
    @Getter
    @Builder
    public static class Item{
        private String name;
        private String mainImage;
        private String content;
        private List<String> images;

        public static FacilityResponse.Item from(Facility facility){
            return Item.builder()
                    .name(facility.getName())
                    .content(facility.getContent())
                    .mainImage(facility.getFacilityImages().get(0).getImageUrl())
                    .images(facility.getFacilityImages().stream()
                            .map(FacilityImage::getImageUrl)
                            .toList())
                    .build();
        }
    }

    public static FacilityResponse from(List<Facility> facilities){
        return FacilityResponse.builder()
                .facilities(facilities.stream()
                        .map(Item::from)
                        .toList())
                .build();
    }
}
