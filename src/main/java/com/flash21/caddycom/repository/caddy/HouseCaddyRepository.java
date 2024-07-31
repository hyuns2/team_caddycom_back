package com.flash21.caddycom.repository.caddy;

import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.entity.caddy.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HouseCaddyRepository extends JpaRepository<HouseCaddy, Long>, HouseCaddyQueryRepository {
    Optional<HouseCaddy> findByPhoneNumber(String phoneNumber);

    @Query("select distinct h.team from HouseCaddy h where h.golfField.id = ?1")
    List<String> findAllTeam(Long golfFieldId);

    List<HouseCaddy> findAllByGolfFieldIdAndTeam(Long golfFieldId, String teamName);

    Optional<HouseCaddy> findByGolfFieldIdAndTeamAndTeamRole(Long golfFieldId, String teamName, TeamRole teamRole);

    @Query("select h from HouseCaddy h where h.golfField.id = ?1")
    List<HouseCaddy> findAllByGolfFieldId(Long golfFieldId);

    List<HouseCaddy> findAllByIdIn(List<Long> ids);
}
