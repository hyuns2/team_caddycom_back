package com.flash21.caddycom.dto.golfField;

import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.golfFieldDetail.Formation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class GolfFieldResponse {
    @AllArgsConstructor
    @Getter
    @Builder
    public static class Overview{
        private String name;
        private String contact;

    }

    @AllArgsConstructor
    @Getter
    @Builder
    public static class Info{
        private String name;
        private String address;
        private String addressDetail;
        private String contact;
        private String fax;
        private String area;
        private LocalDate openingDate;
        private List<String> formations;
        private List<String> courses;
        private String cartInfo;
        private String amenities;


        public static GolfFieldResponse.Info from(GolfField golfField){

            List<String> formations = golfField.getFormations().stream()
                    .map(Formation::getName)
                    .toList();
            List<String> courses = golfField.getFormations().stream()
                    .flatMap(formation -> formation.getCourses().stream())
                    .map(course -> course.getName()+"코스 "+course.getTotalHoles()+"홀")
                    .toList();

            return Info.builder()
                    .name(golfField.getName())
                    .address(golfField.getAddress())
                    .addressDetail(golfField.getAddressDetail())
                    .contact(golfField.getContact())
                    .fax(golfField.getFax())
                    .area(golfField.getArea())
                    .openingDate(golfField.getOpeningDate())
                    .formations(formations)
                    .courses(courses)
                    .cartInfo(golfField.getCartInfo())
                    .amenities(golfField.getAmenities())
                    .build();
        }
    }


    @AllArgsConstructor
    @Getter
    @Builder
    public static class DirectionInfo{
        private String address;
        private String addressDetail;
        private String contact;
        private String publicTransportGuide;
        private String carGuide;
    }
}
