package com.flash21.caddycom.repository.schedule;

import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.schedule.DateStatus;
import com.flash21.caddycom.entity.schedule.ReservationSheet;
import com.flash21.caddycom.entity.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleQueryFactory, ScheduleJdbcRepository {
    Optional<Schedule> findFirstByGolfFieldAndCourseIsInAndReservationAtBetween(GolfField golfField, List<Course> courseList, LocalDate startDate, LocalDate endDate);

    @Query("select s.reservationAt as reservationAt, s.dateStatus as dateStatus, sum(s.totalCnt) as totalCntSum, sum(s.blockedCnt) as blockedCntSum from Schedule s"
            + " where s.reservationAt between :startDate and :endDate and s.golfField.id = :golfFieldId"
            + " group by s.reservationAt, s.dateStatus order by s.reservationAt")
    List<MetaDataReport> findAllMetaDataByMonth(LocalDate startDate, LocalDate endDate, Long golfFieldId);

    @Query("select s from Schedule s left join fetch s.course left join fetch s.assignments"
            + " where s.reservationAt = :date and s.golfField.id = :golfFieldId")
    List<Schedule> findAllWithEntitiesByReservationAtAndGolfFieldId(LocalDate date, Long golfFieldId);

    @Query("select s from Schedule s left join fetch s.course left join fetch s.assignments"
            + " where s in :schedules")
    List<Schedule> findAllWithEntitiesBySchedules(List<Schedule> schedules);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Schedule s set s.dateStatus = :dateStatus where s in :schedules")
    int updateDateStatusBySchedules(DateStatus dateStatus, List<Schedule> schedules);

    List<Schedule> findAllByReservationSheetId(Long reservationSheetId);
    @Modifying(clearAutomatically = true)
    @Query("delete from Schedule s where s.reservationSheet = :reservationSheet")
    int deleteAllByReservationSheet(ReservationSheet reservationSheet);

    @Query("select s from Schedule s where s.reservationAt > :date and s.course.id in :courseIds")
    List<Schedule> findAllByCourseIdsAfterDate(Iterable<Long> courseIds, LocalDate date);

    @Query("SELECT s FROM Schedule s " +
            "JOIN FETCH s.golfField g " +
            "WHERE g.id = :golfFieldId " +
            "AND YEAR(s.reservationAt) = :year " +
            "AND MONTH(s.reservationAt) = :month")
    List<Schedule> findAllByGolfFieldAndDate(Long golfFieldId, int year, int month);
}
