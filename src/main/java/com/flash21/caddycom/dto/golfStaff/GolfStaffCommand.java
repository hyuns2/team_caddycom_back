package com.flash21.caddycom.dto.golfStaff;

import com.flash21.caddycom.entity.account.GolfStaff;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import static com.flash21.caddycom.global.util.Formatter.formatPhoneNumber;
public class GolfStaffCommand {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Create {
        private LocalDate enteringDate;
        private String position;
        private String name;
        private String phoneNumber;
        private String address;

        public static GolfStaffCommand.Create from(GolfStaffRequest.Create request) {
            return Create.builder()
                    .enteringDate(request.getEnteringDate())
                    .position(request.getPosition())
                    .name(request.getName())
                    .phoneNumber(formatPhoneNumber(request.getPhoneNumber()))
                    .address(request.getAddress())
                    .build();
        }

        public GolfStaff toEntity() {
            return GolfStaff.builder()
                    .enteringDate(enteringDate)
                    .position(position)
                    .name(name)
                    .phoneNumber(phoneNumber)
                    .address(address)
                    .build();
        }

    }
}
