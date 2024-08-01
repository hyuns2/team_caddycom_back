package com.flash21.caddycom.repository.schedule;

import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.entity.schedule.Assignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    @Query("select distinct a.startTime from Assignment a"
            + " where a.schedule.reservationAt = ?1 order by a.startTime")
    Page<LocalTime> findTimesByReservationAt(LocalDate date, Pageable pageable);

    @Query("select a from Assignment a"
           + " where a.schedule.reservationAt = ?1 and ?2 <= a.startTime and ?3 >= a.startTime order by a.startTime")
    List<Assignment> findAllByReservationAtAndBetweenTime(LocalDate date, LocalTime startTime, LocalTime endTime);


    @Query("SELECT a FROM Assignment a WHERE a.id IN :ids")
    List<Assignment> findByIds(@Param("ids") List<Long> ids);


    @Modifying
    @Query("UPDATE Assignment a " +
            "SET a.houseCaddy = :caddy, a.caddyName = :caddyName " +
            "WHERE a.id = :assignmentId")
    void switchAssignment(@Param("assignmentId") Long assignmentId, @Param("caddy") HouseCaddy caddy, @Param("caddyName") String caddyName);

}