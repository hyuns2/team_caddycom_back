package com.flash21.caddycom.repository.caddy;

import com.flash21.caddycom.entity.caddy.MatchedFreeCaddy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchedFreeCaddyRepository extends JpaRepository<MatchedFreeCaddy, Long> {
    @Query("SELECT m FROM MatchedFreeCaddy m WHERE m.id IN :ids")
    List<MatchedFreeCaddy> findByIds(@Param("ids") List<Long> ids);
}
