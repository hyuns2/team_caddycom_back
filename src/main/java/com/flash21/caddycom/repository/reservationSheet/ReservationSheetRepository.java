package com.flash21.caddycom.repository.reservationSheet;

import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.reservationSheet.Assignment;
import com.flash21.caddycom.entity.reservationSheet.ReservationDate;
import com.flash21.caddycom.entity.reservationSheet.ReservationSheet;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationSheetRepository extends JpaRepository<ReservationSheet, Long> {
    List<ReservationSheet> findAllByGolfFieldId(Long golfFieldId);

    @Query("select s from ReservationSheet s"
            + " where s.golfField.id = ?1 and s.course.id = ?2"
            + " and ((s.startDateTime between ?3 and ?4) or (s.endDateTime between ?3 and ?4))")
    Optional<ReservationSheet> findOneByGolfFieldIdAndCourseIdAndPartBetweenNewDate(Long golfFieldId, Long courseId, LocalDate startDate, LocalDate endDate);
}
