package com.flash21.caddycom.repository.caddy;

import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.entity.caddy.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HouseCaddyRepository extends JpaRepository<HouseCaddy, Long> {
    Optional<HouseCaddy> findByPhoneNumber(String phoneNumber);

    @Query("select distinct h.team from HouseCaddy h")
    List<String> findAllTeam(Long golfFieldId);

    List<HouseCaddy> findAllByTeam(String teamName);

    Optional<HouseCaddy> findByTeamAndTeamRole(String teamName, TeamRole teamRole);
}
