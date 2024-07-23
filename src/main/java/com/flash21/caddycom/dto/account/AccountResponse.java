package com.flash21.caddycom.dto.account;

import com.flash21.caddycom.entity.account.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class AccountResponse {

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


        public static AccountResponse.Info from(Account account) {
            return Info.builder()
                    .id(account.getId())
                    .enteringDate(account.getEnteringDate())
                    .position(account.getPosition())
                    .name(account.getName())
                    .phoneNumber(account.getPhoneNumber())
                    .address(account.getAddress())
                    .build();
        }
    }
}
