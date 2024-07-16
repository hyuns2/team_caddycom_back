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
        private Long id;
        private String name;
        private String mainImage;
        private String content;
        private List<Image> images;

        @Getter
        @Builder
        public static class Image{
            private Long id;
            private String imageUrl;

            public static Image from(FacilityImage facilityImage){
                return Image.builder()
                        .id(facilityImage.getId())
                        .imageUrl(facilityImage.getImageUrl())
                        .build();
            }
        }

        public static FacilityResponse.Item from(Facility facility){
            String mainImage = facility.getFacilityImages().isEmpty() ?
                    null : facility.getFacilityImages().get(0).getImageUrl();
            return Item.builder()
                    .id(facility.getId())
                    .name(facility.getName())
                    .content(facility.getContent())
                    .mainImage(mainImage)
                    .images(facility.getFacilityImages().stream()
                            .map(Image::from)
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
