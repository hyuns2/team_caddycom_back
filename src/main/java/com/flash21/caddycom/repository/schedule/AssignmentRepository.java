package com.flash21.caddycom.repository.schedule;

import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.entity.schedule.Assignment;
import com.flash21.caddycom.entity.schedule.AssignmentStatus;
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




    // TODO: 코스 이름은 schedule.course 에서 가져올 수 있도록 조인 작업 추가로 필요
    // TODO: JPA 말고 다른 동적 쿼리 생성기 이용
    @Query("SELECT a FROM Assignment a " +
            "WHERE a.schedule.golfField.id = :golfFieldId " +
            "AND a.schedule.reservationAt =:date")
    Page<Assignment> findAllByDate(Pageable pageable, Long golfFieldId, LocalDate date);

    @Query("SELECT a FROM Assignment a " +
            "WHERE a.schedule.golfField.id = :golfFieldId " +
            "AND a.schedule.reservationAt =:date " +
            "AND a.schedule.course.id = :courseId")
    Page<Assignment> findAllByDateAndCourseId(Pageable pageable, Long golfFieldId, LocalDate date, Long courseId);

    @Query("SELECT a FROM Assignment a " +
            "WHERE a.schedule.golfField.id = :golfFieldId " +
            "AND a.schedule.reservationAt =:date " +
            "AND a.status = :status")
    Page<Assignment> findAllByDateAndStatus(Pageable pageable, Long golfFieldId, LocalDate date, AssignmentStatus status);

    @Query("SELECT a FROM Assignment a " +
            "WHERE a.schedule.golfField.id = :golfFieldId " +
            "AND a.schedule.reservationAt =:date " +
            "AND a.schedule.course.id = :courseId " +
            "AND a.status = :status")
    Page<Assignment> findAllByDateAndCourseIdAndStatus(Pageable pageable, Long golfFieldId, LocalDate date, Long courseId, AssignmentStatus status);


    @Query("SELECT a FROM Assignment a " +
            "WHERE a.schedule.golfField.id = :golfFieldId " +
            "AND a.id != :id " +
            "AND a.schedule.reservationAt =:date " +
            "AND a.schedule.course.id = :courseId " +
            "AND a.schedule.part = :part " +
            "AND a.status = 3")
    Page<Assignment> findAssignedByDateAndCourseIdAndPart(Pageable pageable, Long id, Long golfFieldId, LocalDate date, Long courseId, int part);


    @Query("SELECT a FROM Assignment a " +
            "WHERE a.schedule.golfField.id = :golfFieldId " +
            "AND a.id != :id " +
            "AND a.schedule.reservationAt =:date " +
            "AND a.schedule.part = :part " +
            "AND a.status = 3")
    Page<Assignment> findAssignedByDateAndPart(Pageable pageable, Long id, Long golfFieldId, LocalDate date, int part);


    @Query("SELECT a FROM Assignment a " +
            "WHERE a.schedule.golfField.id = :golfFieldId " +
            "AND a.id != :id " +
            "AND a.schedule.reservationAt =:date " +
            "AND a.schedule.course.id = :courseId " +
            "AND a.status = 3")
    Page<Assignment> findAssignedByDateAndCourseId(Pageable pageable, Long id, Long golfFieldId, LocalDate date, Long courseId);


    @Query("SELECT a FROM Assignment a " +
            "WHERE a.schedule.golfField.id = :golfFieldId " +
            "AND a.id != :id " +
            "AND a.schedule.reservationAt =:date " +
            "AND a.status = 3")
    Page<Assignment> findAssignedByDate(Pageable pageable, Long id, Long golfFieldId, LocalDate date);



    @Query("SELECT a FROM Assignment a WHERE a.id IN :ids")
    List<Assignment> findByIds(@Param("ids") List<Long> ids);


    @Modifying
    @Query("UPDATE Assignment a " +
            "SET a.houseCaddy = :caddy, a.caddyName = :caddyName " +
            "WHERE a.id = :assignmentId")
    void switchAssignment(@Param("assignmentId") Long assignmentId, @Param("caddy") HouseCaddy caddy, @Param("caddyName") String caddyName);

}