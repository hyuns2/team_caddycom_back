package com.flash21.caddycom.repository.caddy;

import com.flash21.caddycom.entity.caddy.HouseCaddy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HouseCaddyRepository extends JpaRepository<HouseCaddy, Long> {
        Optional<HouseCaddy> findByPhoneNumber(String phoneNumber);

        Optional<List<HouseCaddy>> findAllByGolfFieldId(Long golfFieldId);

        Optional<List<HouseCaddy>> findAllByGolfFieldIdAndTeam(Long golfFieldId, String team);
}
