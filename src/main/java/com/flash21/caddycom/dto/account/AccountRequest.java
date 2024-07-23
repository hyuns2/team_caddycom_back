package com.flash21.caddycom.dto.account;

import com.flash21.caddycom.entity.account.Account;
import com.flash21.caddycom.entity.golfField.GolfField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class AccountRequest {
    @AllArgsConstructor
    @Builder
    @Getter
    public static class Create {
        private final LocalDate enteringDate;
        private final String position;
        private final String name;
        private final String phoneNumber;
        private final String address;

        public Account toEntity(GolfField golfField) {
            return Account.builder()
                    .enteringDate(enteringDate)
                    .position(position)
                    .name(name)
                    .phoneNumber(phoneNumber)
                    .address(address)
                    .golfField(golfField)
                    .build();
        }
    }
}
