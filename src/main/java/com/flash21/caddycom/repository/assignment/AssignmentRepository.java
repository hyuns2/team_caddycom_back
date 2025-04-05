package com.flash21.caddycom.repository.assignment;

import com.flash21.caddycom.entity.caddy.Caddy;
import com.flash21.caddycom.entity.schedule.Assignment;
import com.flash21.caddycom.entity.schedule.AssignmentStatus;
import com.flash21.caddycom.entity.schedule.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long>, AssignmentQueryFactory, AssignmentJdbcRepository {
    @Query("SELECT a FROM Assignment a WHERE a.id IN :ids")
    List<Assignment> findByIds(@Param("ids") List<Long> ids);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Assignment a " +
            "SET a.caddy = :caddy, a.caddyName = :caddyName " +
            "WHERE a.id = :assignmentId")
    void switchAssignment(@Param("assignmentId") Long assignmentId, @Param("caddy") Caddy caddy, @Param("caddyName") String caddyName);

    Optional<Assignment> findFirstByAssignmentStatusIsInAndScheduleIsIn(List<AssignmentStatus> assignmentStatuses, List<Schedule> schedules);

    List<Assignment> findAllByScheduleIsInAndAssignmentStatusIsIn(List<Schedule> schedules, List<AssignmentStatus> assignmentStatuses);

    @Query("select a from Assignment a where a.schedule in :schedules and (a.assignmentStatus = :assigned or a.assignmentStatus = :blocked)")
    Page<Assignment> findByAssignmentStatusAndSchedule(List<Schedule> schedules, AssignmentStatus assigned, AssignmentStatus blocked, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update Assignment a set a.schedule = null where a in :assignments")
    void updateScheduleToNullByAssignments(List<Assignment> assignments);

    @Modifying
    @Query("delete from Assignment a where a.schedule in :schedules")
    void deleteAllBySchedules(Iterable<Schedule> schedules);
}