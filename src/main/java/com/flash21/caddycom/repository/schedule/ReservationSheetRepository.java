package com.flash21.caddycom.repository.schedule;

import com.flash21.caddycom.entity.schedule.ReservationSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationSheetRepository extends JpaRepository<ReservationSheet, Long> {
    List<ReservationSheet> findAllByGolfFieldId(Long golfFieldId);

    @Query("select rs from ReservationSheet rs where rs.startDate > :date")
    List<ReservationSheet> findAllByAfterDate(LocalDate date);
}
