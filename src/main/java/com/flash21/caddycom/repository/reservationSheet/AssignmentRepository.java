package com.flash21.caddycom.repository.reservationSheet;

import com.flash21.caddycom.entity.reservationSheet.Assignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    @Query("select distinct a.startTime from Assignment a"
            + " where a.reservationDate.reservationAt = ?1 order by a.startTime")
    Page<LocalTime> findTimesByReservationDate(LocalDate date, Pageable pageable);

    @Query("select a from Assignment a"
            + " where a.reservationDate.reservationAt = ?1 and ?2 <= a.startTime and ?3 >= a.startTime order by a.startTime")
    List<Assignment> findAllByReservationDateAndBetweenTime(LocalDate date, LocalTime startTime, LocalTime endTime);

//    @Query("SELECT a FROM Assignment a " +
//            "WHERE a.reservationDate.reservationAt =:date")
//    Page<Assignment> findAllByReservationDateAndGolfFieldId(Long golfFieldId, LocalDate date);
}