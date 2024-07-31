package com.flash21.caddycom.repository.schedule;

import com.flash21.caddycom.entity.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("select s from Schedule s"
            + " where s.golfField.id = ?1 and s.course.id = ?2"
            + " and s.reservationAt between ?3 and ?4")
    List<Schedule> findAllByGolfFieldIdAndCourseIdBetweenNewDate(Long golfFieldId, Long courseId, LocalDate startDate, LocalDate endDate);
    @Query("select s.reservationAt as reservationAt, s.dateStatus as dateStatus, sum(s.totalCnt) as totalCntSum, sum(s.blockedCnt) as blockedCntSum from Schedule s"
            + " where s.reservationAt between ?1 and ?2"
            + " and s.golfField.id = ?3 group by s.reservationAt, s.dateStatus")
    List<MetaDataReport> countAllMetaDataByDate(LocalDate startDate, LocalDate endDate, Long golfFieldId);

    List<Schedule> findAllByGolfFieldIdAndReservationAt(Long golfFieldId, LocalDate date);
}
