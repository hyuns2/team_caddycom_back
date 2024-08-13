package com.flash21.caddycom.repository.caddy;

import com.flash21.caddycom.entity.caddy.FreeCaddy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FreeCaddyRepository extends JpaRepository<FreeCaddy, Long> {
    Optional<FreeCaddy> findByPhoneNumber(String phoneNumber);
}
