package com.flash21.caddycom.dto.account;

import com.flash21.caddycom.entity.account.Account;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class AccountRequest {
    @AllArgsConstructor
    @Builder
    @Getter
    public static class Create {
        private final LocalDate enteringDate;

        private final String position;

        private final String name;

        @NotBlank(message = "phoneNumber 는 필수값입니다.")
        private final String phoneNumber;

        private final String address;

        //TODO: 전화번호 파싱 정규식 공통 메서드 추출 필요
        private String formatPhoneNumber(String phoneNumber) {
            return phoneNumber.replaceAll("[^0-9]", "");
        }

        public Account toEntity() {
            return Account.builder()
                    .enteringDate(enteringDate)
                    .position(position)
                    .name(name)
                    .phoneNumber(formatPhoneNumber(phoneNumber))
                    .address(address)
                    .build();
        }
    }
}
