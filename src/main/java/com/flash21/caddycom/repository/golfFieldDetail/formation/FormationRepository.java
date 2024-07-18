package com.flash21.caddycom.repository.golfFieldDetail.formation;

import com.flash21.caddycom.entity.golfFieldDetail.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FormationRepository extends JpaRepository<Formation, Long> {
    @Query("select distinct f from Formation f join fetch f.courses where f.golfField.id = :id" )
    Optional<List<Formation>> findAllByGolfFieldId(Long id);
}
