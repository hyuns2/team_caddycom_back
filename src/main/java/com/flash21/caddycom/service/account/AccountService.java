package com.flash21.caddycom.service.account;

import com.flash21.caddycom.dto.account.AccountRequest;
import com.flash21.caddycom.dto.account.AccountResponse;
import com.flash21.caddycom.entity.account.Account;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.repository.account.AccountRepository;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final GolfFieldRepository golfFieldRepository;

    @Transactional(readOnly = true)
    public List<AccountResponse.Info> getAccounts(Long id) {
        GolfField golfField = golfFieldRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("골프장이 존재하지 않습니다."));

        List<Account> accounts = accountRepository.findAllByGolfField(golfField);
        return accounts.stream()
                .map(AccountResponse.Info::from)
                .toList();
    }

    @Transactional
    public void createAccounts(Long id, List<AccountRequest.Create> request) {
        GolfField golfField = golfFieldRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("골프장이 존재하지 않습니다."));

        List<Account> accounts = request.stream()
                .map(account -> account.toEntity(golfField)).toList();
        accountRepository.saveAll(accounts);
    }
}
