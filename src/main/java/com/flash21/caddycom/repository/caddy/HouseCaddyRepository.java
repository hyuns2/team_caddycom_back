package com.flash21.caddycom.repository.caddy;

import com.flash21.caddycom.entity.caddy.HouseCaddy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseCaddyRepository extends JpaRepository<HouseCaddy, Long> {
}
