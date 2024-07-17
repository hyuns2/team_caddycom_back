package com.flash21.caddycom.repository.reservationSheet;

import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.reservationSheet.Assignment;
import com.flash21.caddycom.entity.reservationSheet.ReservationDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    @Query("select a from Assignment a"
            + " where a.reservationDate.reservationSheet.course = ?1 and a.reservationDate.reservationAt = ?2")
    Page<Assignment> findAllByCourseAndReservationDate(Course course, LocalDate date, Pageable pageable);

    @Query("select a from Assignment a"
            + " where a.reservationDate.reservationSheet.course = ?1 and a.reservationDate.reservationSheet.part =?2 and a.reservationDate.reservationAt = ?3")
    Page<Assignment> findAllByCourseAndPartAndReservationDate(Course course, int part, LocalDate date, Pageable pageable);
}
