package com.flash21.caddycom.dto.golfStaff;

import com.flash21.caddycom.entity.account.GolfStaff;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class GolfStaffResponse {

    @AllArgsConstructor
    @Builder
    @Getter
    public static class Info {
        private final Long id;
        private final LocalDate enteringDate;
        private final String position;
        private final String name;
        private final String phoneNumber;
        private final String address;


        public static GolfStaffResponse.Info from(GolfStaff golfStaff) {
            return Info.builder()
                    .id(golfStaff.getId())
                    .enteringDate(golfStaff.getEnteringDate())
                    .position(golfStaff.getPosition())
                    .name(golfStaff.getName())
                    .phoneNumber(golfStaff.getPhoneNumber())
                    .address(golfStaff.getAddress())
                    .build();
        }
    }
}
