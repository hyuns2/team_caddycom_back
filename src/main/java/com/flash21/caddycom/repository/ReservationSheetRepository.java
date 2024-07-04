package com.flash21.caddycom.repository;

import com.flash21.caddycom.entity.ReservationSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationSheetRepository extends JpaRepository<ReservationSheet, Long> {
}
