package com.flash21.caddycom.repository.reservationSheet;

import com.flash21.caddycom.entity.reservationSheet.ReservationSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationSheetRepository extends JpaRepository<ReservationSheet, Long> {
    List<ReservationSheet> findAllByGolfFieldId(Long golfFieldId);

    @Query("select s from ReservationSheet s"
            + " where s.golfField.id = ?1 and s.course.id = ?2"
            + " and ((s.startDateTime between ?3 and ?4) or (s.endDateTime between ?3 and ?4))")
    List<ReservationSheet> findAllByGolfFieldIdAndCourseIdBetweenNewDate(Long golfFieldId, Long courseId, LocalDateTime startDate, LocalDateTime endDate);
}
