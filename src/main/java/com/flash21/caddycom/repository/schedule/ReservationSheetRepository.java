package com.flash21.caddycom.repository.schedule;

import com.flash21.caddycom.entity.schedule.ReservationSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationSheetRepository extends JpaRepository<ReservationSheet, Long> {
    List<ReservationSheet> findAllByGolfFieldId(Long golfFieldId);
}
