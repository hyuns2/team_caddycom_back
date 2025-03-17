package com.flash21.caddycom.repository.schedule;

import com.flash21.caddycom.entity.schedule.ReservationSheet;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationSheetRepository extends JpaRepository<ReservationSheet, Long> {
    List<ReservationSheet> findAllByGolfFieldId(Long golfFieldId);

    @EntityGraph(attributePaths = { "golfField", "schedules" })
    Optional<ReservationSheet> findWithEntitiesById(Long id);

    @Query("select rs from ReservationSheet rs left join fetch Schedule s where rs.id = :id")
    Optional<ReservationSheet> findWithSchedulesById(Long id);

    @Query("select rs from ReservationSheet rs where rs.startDate > :date")
    List<ReservationSheet> findAllByAfterDate(LocalDate date);
}
