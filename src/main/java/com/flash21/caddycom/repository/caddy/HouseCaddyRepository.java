package com.flash21.caddycom.repository.caddy;

import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.entity.caddy.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HouseCaddyRepository extends JpaRepository<HouseCaddy, Long>, HouseCaddyJdbcRepository, HouseCaddyQueryFactory {

    Optional<HouseCaddy> findByPhoneNumber(String phoneNumber);

    @Query("select distinct COALESCE(h.team,'조 없음') from HouseCaddy h where h.golfField.id = ?1 " +
            "ORDER BY COALESCE(h.team, '조 없음') ASC")
    List<String> findAllTeam(Long golfFieldId);

    List<HouseCaddy> findAllByGolfFieldIdAndTeam(Long golfFieldId, String teamName);

    Optional<HouseCaddy> findByGolfFieldIdAndTeamAndTeamRole(Long golfFieldId, String teamName, TeamRole teamRole);

    @Query("select h from HouseCaddy h where h.golfField.id = ?1")
    List<HouseCaddy> findAllByGolfFieldId(Long golfFieldId);

    List<HouseCaddy> findAllByIdIn(List<Long> ids);

    @Query("select hc from HouseCaddy hc where hc.golfField.id = ?1 order by hc.id ASC")
    List<HouseCaddy> findAllByGolfFieldIdSortById(Long golfFieldId);
}
