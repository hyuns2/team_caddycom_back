package com.flash21.caddycom.repository.golfFieldDetail.formation;

import com.flash21.caddycom.entity.golfFieldDetail.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FormationRepository extends JpaRepository<Formation, Long> {
    @Query("select distinct f from Formation f LEFT join fetch f.courses where f.golfField.id = :id" )
    List<Formation> findAllByGolfFieldId(Long id);
}
