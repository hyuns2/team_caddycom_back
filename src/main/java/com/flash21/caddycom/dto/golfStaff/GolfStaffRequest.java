package com.flash21.caddycom.dto.golfStaff;

import com.flash21.caddycom.global.validation.PhoneNumber;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class GolfStaffRequest {
    @AllArgsConstructor
    @Builder
    @Getter
    public static class Create {
        private final LocalDate enteringDate;

        private final String position;

        private final String name;

        @NotBlank(message = "phoneNumber 는 필수값입니다.")
        @PhoneNumber
        private final String phoneNumber;

        private final String address;
    }
}
