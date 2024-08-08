package com.flash21.caddycom.repository.caddy;

import com.flash21.caddycom.entity.caddy.Caddy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CaddyRepository extends JpaRepository<Caddy, Long> {
    Optional<Caddy> findByPhoneNumber(String phoneNumber);
}
