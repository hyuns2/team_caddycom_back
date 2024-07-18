package com.flash21.caddycom.repository;

import com.flash21.caddycom.entity.golfFieldDetail.TipInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TipInfoRepository extends JpaRepository<TipInfo, Long> {
    @Query("select t from TipInfo t where t.hole.id = :holeId")
    Optional<List<TipInfo>> findAllByHoleId(Long holeId);
}
