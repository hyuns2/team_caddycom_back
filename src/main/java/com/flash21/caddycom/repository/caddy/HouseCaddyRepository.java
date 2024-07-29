package com.flash21.caddycom.repository.caddy;

import com.flash21.caddycom.entity.caddy.HouseCaddy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HouseCaddyRepository extends JpaRepository<HouseCaddy, Long> {
    @Query("select distinct h.team from HouseCaddy h")
    List<String> findAllTeam(Long golfFieldId);

    Page<HouseCaddy> findAllByTeam(String teamName, Pageable page);
}
