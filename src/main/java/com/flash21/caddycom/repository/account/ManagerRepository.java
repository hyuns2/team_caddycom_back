package com.flash21.caddycom.repository.account;

import com.flash21.caddycom.entity.account.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
    Optional<Manager> findByPhoneNumber(String phoneNumber);
}
