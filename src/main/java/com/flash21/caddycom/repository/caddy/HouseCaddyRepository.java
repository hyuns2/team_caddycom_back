package com.flash21.caddycom.repository.caddy;

import com.flash21.caddycom.entity.caddy.HouseCaddy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HouseCaddyRepository extends JpaRepository<HouseCaddy, Long> {
        Optional<HouseCaddy> findByPhoneNumber(String phoneNumber);
}
