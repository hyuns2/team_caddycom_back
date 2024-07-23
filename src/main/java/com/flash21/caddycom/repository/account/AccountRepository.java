package com.flash21.caddycom.repository.account;

import com.flash21.caddycom.entity.account.Account;
import com.flash21.caddycom.entity.golfField.GolfField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByPhoneNumber(String phoneNumber);
    List<Account> findAllByGolfField(GolfField golfField);
}
