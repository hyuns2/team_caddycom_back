package com.flash21.caddycom.repository.reservationSheet;

import com.flash21.caddycom.entity.reservationSheet.ReservationSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationSheetRepository extends JpaRepository<ReservationSheet, Long> {
}
